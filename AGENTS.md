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
| POST    | `/api/challenges/{challengeId}/runs`    | `registerRun(String, RegisterChallengeRunRequest)` | `IdResponse`             |
| GET     | `/api/challenges/{challengeId}`         | `getChallengeById(String)`                | `ChallengeResponse`               |

**Runs asynchrones** : chaque participant peut enregistrer le `gameId` de son run
(`POST /runs`, joueur issu du JWT). `ChallengeResponse` expose `challengerGameId` /
`challengedGameId` / `replayGameId` ; le premier run enregistré sert de référence au replay de
l'adversaire (mêmes questions), et le **second run** enregistré est le `replayGameId` (résultat
autoritaire). Le `gameId` « synchrone » reste celui créé à l'acceptation.

### `TopicFollowerController` — `/api/topic-follows`

Suivi **non destructif** : le `followId` est déterministe (`userId + ":" + topicId`), un follow sur
une même `(user, topic)` est idempotent, et l'unfollow ne supprime pas la ligne — il pose
`unfollowedAt` (l'agrégat reste rechargable pour être re-suivi). La projection
`TopicFollowerProjection` est la source de vérité de l'état de suivi.

| Méthode | Chemin                          | Handler                          | Response                              |
|---------|---------------------------------|----------------------------------|---------------------------------------|
| POST    | `/api/topic-follows/search`     | `search(SearchRequest)`          | `PageResponse<TopicFollowerResponse>` |
| POST    | `/api/topic-follows`            | `follow(FollowTopicRequest)`     | `IdResponse`                          |
| DELETE  | `/api/topic-follows/{followId}` | `unfollow(String)`               | `IdResponse`                          |

**Pas d'endpoint de vérification** : le client détermine ce que suit l'utilisateur via
`POST /search` (filtres `topicId` / `userId`).

### `UserFollowerController` — `/api/user-follows`

Suivi **unidirectionnel** d'un joueur (aucune demande ni acceptation). `followId` généré côté
controller ; suivi idempotent protégé par contrainte unique `(follower_id, followed_id)`. On ne peut
pas se suivre soi-même. Unfollow = suppression, re-follow possible.

| Méthode | Chemin                                | Handler                     | Response                             |
|---------|---------------------------------------|-----------------------------|--------------------------------------|
| POST    | `/api/user-follows/search`            | `search(SearchRequest)`     | `PageResponse<UserFollowerResponse>` |
| POST    | `/api/user-follows` (`{followedId}`)  | `follow(FollowUserRequest)` | `IdResponse`                         |
| DELETE  | `/api/user-follows/{followId}`        | `unfollow(String)`          | `IdResponse`                         |

**Abonnements / abonnés / compteurs** ne sont pas des endpoints dédiés : le client interroge
`POST /search` avec les filtres `followerId` (mes abonnements) ou `followedId` (mes abonnés) et lit
`totalElements` pour les compteurs.

---

## 3. Use cases (ports entrants — `domain/port/in/`)

- Challenges : `CreateChallengeUseCase`, `AcceptChallengeUseCase`, `DeclineChallengeUseCase`,
  `GetChallengeUseCase`, `SearchChallengeUseCase`
- Topic followers : `FollowTopicUseCase`, `UnfollowTopicUseCase`, `SearchTopicFollowerUseCase`
- User followers : `FollowUserUseCase`, `UnfollowUserUseCase`, `SearchUserFollowerUseCase`

---

## 4. Dépendances inter-services

| Port out    | Service cible     | Query Axon envoyée (QueryGateway)                          |
|-------------|-------------------|------------------------------------------------------------|
| `TopicRepositoryPort` | `quizup-theme`    | `TopicQuery.TopicExistsByIdQuery`                          |
| `ProfileRepositoryPort`  | `quizup-profile`    | `ProfileQuery.ProfileExistsByIdQuery`, `ProfileQuery.FindProfileQuery` |

Implémentations dans `application/service/` : `TopicService` (→ theme), `ProfileService`
(→ profile, nom via `Profile::displayName`).

**Ports sortants locaux** : `ChallengeRepositoryPort`, `TopicFollowerRepositoryPort`,
`UserFollowerRepositoryPort`.

### Fourniture (sortant)

- `UserFollowerQuery.SearchUserFollowerQuery` (filtre `followerId`) : consommée par
  `quizup-leaderboard` (portée « abonnements » du classement) — **réutilise la recherche**, pas de
  query dédiée.
- Follows (topics et joueurs) : lecture **exclusivement via `POST /search`** + filtres (calcul client).
- `ChallengeResponse` expose `gameId`.
