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
  `/api/challenges` (surface BFF)
- **TopicFollower** : abonnements aux topics (follow/unfollow)
- **UserFollower** : abonnements aux joueurs (follow/unfollow, unidirectionnel)

**Package** : `io.github.quizup.social`

> Les **amitiés / demandes d'amitié** (FriendRequest/Friendship) ont été **retirées** du service
> (modèle « follow » uniquement). Les tables legacy (`friendship` / `friend_request`) ne sont plus
> créées : le schéma consolidé `V1__create_social_tables.sql` ne contient que `challenge_entry`,
> `topic_follower` et `user_follower`.

---

## 2. Surface (headless)

Service **headless** : aucun contrôleur REST ni WebSocket. La surface applicative unique est le
**`quizup-bff`** (`/api/**` + `/ws`) ; il interroge ce service via le **query bus** Axon et consomme
ses événements. Les handlers de requête/commande, sagas et projections restent la seule surface
exposée par le service.
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
