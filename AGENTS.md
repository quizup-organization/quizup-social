# AGENTS.md — quizup-social

> Service de **social** : abonnements aux topics et aux joueurs, modèle « follow »
> unidirectionnel (sans amitié ni demande). Architecture : Axon Framework (CQRS/EDA) + JPA
> (projections).
> Pour les règles de patterns : [
`../../best-practices/.backend/folder-structure.md`](../../best-practices/.backend/folder-structure.md).

---

## 1. Rôle

Gestion des relations sociales entre joueurs :

- **TopicFollower** : abonnements aux topics (follow/unfollow)
- **UserFollower** : abonnements aux joueurs (follow/unfollow, unidirectionnel)

> Les **défis (challenges)** ont été **retirés** du service : un défi nominatif est désormais un
> **salon privé** géré par `quizup-matchmaking` (surface BFF `/api/lobbies { topicId, opponentId }`).
> Les tables legacy `challenge_entry` ne sont plus créées.

**Package** : `io.github.quizup.social`

> Les **amitiés / demandes d'amitié** (FriendRequest/Friendship) ont été **retirées** du service
> (modèle « follow » uniquement). Le schéma consolidé `V1__create_social_tables.sql` ne contient
> que `topic_follower` et `user_follower`.

---

## 2. Surface (headless)

Service **headless** : aucun contrôleur REST ni WebSocket. La surface applicative unique est le
**`quizup-bff`** (`/api/**` + `/ws`) ; il interroge ce service via le **query bus** Axon et consomme
ses événements. Les handlers de requête/commande, sagas et projections restent la seule surface
exposée par le service.
## 3. Use cases (ports entrants — `domain/port/in/`)

- Topic followers : `FollowTopicUseCase`, `UnfollowTopicUseCase`, `GetTopicFollowerUseCase`,
  `SearchTopicFollowerUseCase`
- User followers : `FollowUserUseCase`, `UnfollowUserUseCase`, `GetUserFollowerUseCase`,
  `SearchUserFollowerUseCase`

---

## 4. Dépendances inter-services

| Port out    | Service cible     | Query Axon envoyée (QueryGateway)                          |
|-------------|-------------------|------------------------------------------------------------|
| `ProfileRepositoryPort`  | `quizup-profile`    | `ProfileQuery.ProfileExistsByIdQuery`, `ProfileQuery.GetProfileQuery` |

Implémentation dans `application/service/` : `ProfileService`.

> Le suivi de sujet ne valide **plus** l'existence du topic (pas de requête synchrone vers
> `quizup-theme` sur le chemin d'écriture) : le `topicId` vient d'un sujet affiché par le client et
> la projection theme ignore les topics inconnus.

**Ports sortants locaux** : `TopicFollowerRepositoryPort`, `UserFollowerRepositoryPort`.

### Fourniture (sortant)

- **Queries dédiées aux vues BFF** (le BFF ne compose plus de page à partir du search) :
  - `UserFollowerQuery.GetUserFollowCountsQuery(userId)` → `UserFollowCounts` ;
    `GetUserFollowsQuery(userId, direction, limit)` ; `ExistsUserFollowerQuery(followerId, followedId)`.
  - `TopicFollowerQuery.GetTopicFollowsQuery(userId, limit)` ;
    `ExistsTopicFollowerQuery(topicId, userId)`.
- `Search*Query` restent dans le domaine pour les **futures surfaces d'administration** ;
  `quizup-leaderboard` utilise désormais la query dédiée `GetUserFollowsQuery` pour la portée
  « abonnements ».
- **Désabonnement authentifié** : `UnfollowUserCommand(followId, actorId)` /
  `UnfollowTopicCommand(followId, actorId)` vérifient que l'acteur est bien propriétaire du suivi
  (`NotFollowOwnerProblem`, catégorie `PERMISSION`).
- `UserFollowedEvent` alimente le service `quizup-notification` (notification `FOLLOW`).
