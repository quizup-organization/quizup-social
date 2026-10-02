# AGENTS.md — quizup-social

> Service de **social** : défis (challenges) 1v1 et abonnements (topics + joueurs), modèle
> « follow » unidirectionnel (sans amitié ni demande). Architecture : Axon Framework (CQRS/EDA) +
> JPA (projections).
> Pour les règles de patterns : [
`../../best-practices/.backend/hexagonal-architecture.md`](../../best-practices/.backend/hexagonal-architecture.md).

---

## 1. Rôle

Gestion des relations sociales entre joueurs :

- **TopicFollower** : abonnements aux topics (follow/unfollow)
- **UserFollower** : abonnements aux joueurs (follow/unfollow, unidirectionnel)

> Les **défis (challenges)** ont été **retirés** du service : un défi est désormais un **salon
> privé** géré par `quizup-matchmaking` (surface BFF `/api/lobbies`). Les tables legacy
> `challenge_entry` ne sont plus créées.

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
| `ProfileRepositoryPort`  | `quizup-profile`    | `ProfileQuery.ProfileExistsByIdQuery`, `ProfileQuery.GetProfileQuery` |

Implémentation dans `application/service/` : `ProfileService`
(→ profile, pseudonyme + langue via `Profile::pseudonym` / `Profile::language`).

**Garde linguistique** : `ChallengeAggregate` (création **et** acceptation) vérifie via
`ProfileRepositoryPort` + `TopicAvailabilityPort` (query theme
`CountApprovedQuestionsByTopicAndLanguagesQuery`) que le thème couvre les langues des deux joueurs
— sinon `TopicNotAvailableInLanguageProblem`. La `ChallengeSaga` transmet l'union des langues à
`GameCommand.CreateGameCommand` (sélection stricte côté game) et émet `FailChallengeCommand`
(→ `ChallengeFailedEvent`, statut `CANCELED`) si la création de partie échoue malgré tout.

> Le suivi de sujet ne valide **plus** l'existence du topic (pas de requête synchrone vers
> `quizup-theme` sur le chemin d'écriture) : le `topicId` vient d'un sujet affiché par le client et
> la projection theme ignore les topics inconnus.

**Ports sortants locaux** : `ChallengeRepositoryPort`, `TopicFollowerRepositoryPort`,
`UserFollowerRepositoryPort`.

### Fourniture (sortant)

- **Queries dédiées aux vues BFF** (le BFF ne compose plus de page à partir du search) :
  - `ChallengeQuery.GetChallengeBoxQuery(userId, box, status, page, size)` → `ChallengePage`
    (reçus/envoyés/les deux, statut optionnel) ; `CountPendingChallengesQuery(userId)`.
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
- `ChallengeResponse` expose `gameId`.

### Fin de défi (C2)

- **Runs asynchrones validés** : `RegisterChallengeRunCommand` vérifie via
  `ChallengeRunGamePort` (query dédiée `GameQuery.GetGameRunInfoQuery`) que la partie est un run
  `ASYNC` du même sujet appartenant au joueur.
- **Acceptation refusée si expiré** (`ChallengeExpiredProblem`) : la deadline de la saga n'est
  qu'un déclencheur, la garde d'état est dans l'agrégat.
- **Complétion et vainqueur** : `ChallengeRunResultHandler` (`@ProcessingGroup
  challenge-run-result`) consomme `GameRunRecordedEvent` / `GameEndedEvent` du bus partagé et
  route vers `RecordChallengeRunResultCommand` (deux scores connus ⇒ `COMPLETED`) ou
  `CompleteChallengeCommand` (partie synchrone d'un défi accepté). Le vainqueur est le meilleur
  score (`null` en cas d'égalité) ; `ChallengeCompletedEvent` notifie les deux joueurs.
- `ChallengeQuery.FindChallengeByGameIdQuery` retrouve le défi lié à une partie (sync ou run).
