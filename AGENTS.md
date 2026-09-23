# AGENTS.md — quizup-social

> Service de **social** : défis (challenges) 1v1 et abonnements (topics + joueurs), modèle
> « follow » unidirectionnel (sans amitié ni demande). Architecture : Axon Framework (CQRS/EDA) +
> JPA (projections).
> Pour les règles de patterns : [
`../../best-practices/.backend/hexagonal-architecture.md`](../../best-practices/.backend/hexagonal-architecture.md).

---

## 1. Rôle

Gestion des relations sociales entre joueurs :

- **Challenges** : défis 1v1 (création, accept/reject) — le frontend les appelle via
  `/social-service/api/challenges`
- **TopicFollower** : abonnements aux topics (follow/unfollow)
- **UserFollower** : abonnements aux joueurs (follow/unfollow, unidirectionnel)

**Package** : `io.github.quizup.social`

> Les **amitiés / demandes d'amitié** (FriendRequest/Friendship) ont été **retirées** du service
> (modèle « follow » uniquement). Les tables legacy (`friendship` / `friend_request`) ne sont plus
> créées : le schéma consolidé `V1__create_social_tables.sql` ne contient que `challenge_entry`,
> `topic_follower` et `user_follower`.

---

## 2. Endpoints REST

### `ChallengeController` — `/api/challenges`

| Méthode | Chemin                                  | Handler                                   | Response                          |
|---------|-----------------------------------------|-------------------------------------------|-----------------------------------|
| POST    | `/api/challenges/search`                | `search(SearchRequest)`                   | `PageResponse<ChallengeResponse>` |
| POST    | `/api/challenges`                       | `createChallenge(CreateChallengeRequest)` | `IdResponse`                      |
| POST    | `/api/challenges/{challengeId}/accept`  | `acceptChallenge(String)`                 | `IdResponse`                      |
| POST    | `/api/challenges/{challengeId}/decline` | `declineChallenge(String)`                | `IdResponse`                      |
| POST    | `/api/challenges/{challengeId}/cancel`  | `cancelChallenge(String)`                 | `IdResponse`                      |
| POST    | `/api/challenges/{challengeId}/runs`    | `registerRun(String, RegisterChallengeRunRequest)` | `IdResponse`              |
| GET     | `/api/challenges/{challengeId}`         | `getChallengeById(String)`                | `ChallengeResponse`               |

**Annulation** : `POST /api/challenges/{challengeId}/cancel` (par l'`challengerId`, tant que
`PENDING`) — c'est une **transition d'état** (`→ CANCELED`), pas une suppression de ressource
(cf. `best-practices/.backend/rest-api.md` § transitions).

**Autorisation** : `accept`/`decline` sont réservés au **joueur défié**, `cancel` au **joueur
instigateur** (`challengerId`), tant que le défi est `PENDING` (l'acteur `SecurityHelper.getUserId()`
est porté par la commande ; l'agrégat rejette tout autre appelant via `UnauthorizedChallengeActionProblem`,
et tout appel hors `PENDING` via `ChallengeNotPendingProblem`).

**Runs asynchrones** : chaque participant enregistre le `gameId` de son run via
`POST /api/challenges/{challengeId}/runs` (action sur l'agrégat, joueur issu du JWT) → `200
IdResponse(gameId)`. Le run n'est **pas** une ressource adressable : ses ids sont exposés par
`ChallengeResponse` (`challengerGameId` / `challengedGameId` / `replayGameId`). Le premier run
enregistré sert de référence au replay de l'adversaire (mêmes questions), le **second** est le
`replayGameId` (résultat autoritaire) ; le `gameId` « synchrone » reste celui créé à l'acceptation.

### `TopicFollowerController` — `/api/topic-follows`

Suivi **non destructif** : le `followId` de l'agrégat est déterministe (`userId + ":" + topicId`),
un follow sur une même `(user, topic)` cible donc le **même agrégat** (Axon sérialise par id) :
follow/unfollow sont **idempotents** et le re-suivi ne crée pas de collision d'id. L'agrégat n'est
**jamais supprimé** (état `followed`). Le read model `topic_follower` reste la source de vérité de
l'état de suivi : la projection **upsert/delete par clé naturelle** (ligne présente = suivi),
idempotente et rejouable.

| Méthode | Chemin                          | Handler                          | Response                              |
|---------|---------------------------------|----------------------------------|---------------------------------------|
| POST    | `/api/topic-follows/search`     | `search(SearchRequest)`          | `PageResponse<TopicFollowerResponse>` |
| GET     | `/api/topic-follows/{followId}` | `getById(String)`                | `TopicFollowerResponse` / 404         |
| POST    | `/api/topic-follows`            | `follow(FollowTopicRequest)`     | `201 + Location`                      |
| DELETE  | `/api/topic-follows/{followId}` | `unfollow(String)`               | `204 No Content`                      |

**Lecture par id** : le client lit l'état de suivi par l'id déterministe
`userId + ":" + topicId` via `GET /{followId}` (plus de recherche filtrée pour un suivi précis —
cf. `best-practices/.backend/rest-api.md`).

### `UserFollowerController` — `/api/user-follows`

Suivi **unidirectionnel** d'un joueur (aucune demande ni acceptation). Le `followId` de l'agrégat
est déterministe (`followerId + ":" + followedId`) : follow/unfollow sont **idempotents** et le
re-suivi ne crée pas de collision d'id (l'agrégat n'est jamais supprimé — état `followed`). Le read
model `user_follower` (contrainte unique `(follower_id, followed_id)`) reste la source de vérité de
l'état de suivi. On ne peut pas se suivre soi-même.

| Méthode | Chemin                                | Handler                     | Response                             |
|---------|---------------------------------------|-----------------------------|--------------------------------------|
| POST    | `/api/user-follows/search`            | `search(SearchRequest)`     | `PageResponse<UserFollowerResponse>` |
| GET     | `/api/user-follows/{followId}`        | `getById(String)`           | `UserFollowerResponse` / 404         |
| POST    | `/api/user-follows` (`{followedId}`)  | `follow(FollowUserRequest)` | `201 + Location`                     |
| DELETE  | `/api/user-follows/{followId}`        | `unfollow(String)`          | `204 No Content`                     |

**Lecture par id** : état de suivi lu par l'id déterministe `followerId + ":" + followedId`
via `GET /{followId}`. **Abonnements / abonnés / compteurs** : `POST /search` avec les filtres
`followerId` (mes abonnements) ou `followedId` (mes abonnés), `totalElements` pour les compteurs.

---

## 3. Use cases (ports entrants — `domain/port/in/`)

- Challenges : `CreateChallengeUseCase`, `AcceptChallengeUseCase`, `DeclineChallengeUseCase`,
  `CancelChallengeUseCase`, `RegisterChallengeRunUseCase`, `GetChallengeUseCase`,
  `SearchChallengeUseCase`
- Topic followers : `FollowTopicUseCase`, `UnfollowTopicUseCase`, `GetTopicFollowerUseCase`,
  `SearchTopicFollowerUseCase`
- User followers : `FollowUserUseCase`, `UnfollowUserUseCase`, `GetUserFollowerUseCase`,
  `SearchUserFollowerUseCase`

---

## 4. Dépendances inter-services

| Port out    | Service cible     | Query Axon envoyée (QueryGateway)                          |
|-------------|-------------------|------------------------------------------------------------|
| `ProfileRepositoryPort`  | `quizup-profile`    | `ProfileQuery.ProfileExistsByIdQuery`, `ProfileQuery.FindProfileQuery` |

Implémentation dans `application/service/` : `ProfileService`
(→ profile, nom via `Profile::displayName`).

> Le suivi de sujet ne valide **plus** l'existence du topic (pas de requête synchrone vers
> `quizup-theme` sur le chemin d'écriture) : le `topicId` vient d'un sujet affiché par le client et
> la projection theme ignore les topics inconnus.

**Ports sortants locaux** : `ChallengeRepositoryPort`, `TopicFollowerRepositoryPort`,
`UserFollowerRepositoryPort`.

### Fourniture (sortant)

- `UserFollowerQuery.SearchUserFollowerQuery` (filtre `followerId`) : consommée par
  `quizup-leaderboard` (portée « abonnements » du classement) — **réutilise la recherche**, pas de
  query dédiée.
- Follows (topics et joueurs) : lecture **exclusivement via `POST /search`** + filtres (calcul client).
- `ChallengeResponse` expose `gameId`.
