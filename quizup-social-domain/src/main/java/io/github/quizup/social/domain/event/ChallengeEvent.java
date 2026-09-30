package io.github.quizup.social.domain.event;

import java.time.Instant;

public interface ChallengeEvent {

    String challengeId();

    /**
     * Event publié quand un défi expire (24h sans réponse).
     */
    record ChallengeExpiredEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            Instant expiredAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un défi est refusé.
     */
    record ChallengeDeclinedEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            Instant declinedAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un défi est annulé par son instigateur (avant acceptation).
     */
    record ChallengeCanceledEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            Instant canceledAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un défi est créé.
     * Pas de gameId ici — la partie n'est créée que quand le défi est accepté.
     */
    record ChallengeCreatedEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            String topicId,
            Instant createdAt,
            Instant expiresAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un défi est accepté.
     * Le gameId est généré à ce moment — la saga crée ensuite la partie.
     */
    record ChallengeAcceptedEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            String gameId,
            Instant acceptedAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un défi accepté ne peut pas aboutir à une partie
     * (échec de création de partie). Le défi est alors clos ({@code CANCELED}).
     */
    record ChallengeFailedEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            String reason,
            Instant failedAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un participant enregistre son run asynchrone.
     */
    record ChallengeRunRegisteredEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            String playerId,
            String gameId,
            Instant registeredAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand le score final d'un run asynchrone est connu.
     */
    record ChallengeRunResultRecordedEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            String playerId,
            int score,
            Instant recordedAt
    ) implements ChallengeEvent {
    }

    /**
     * Event publié quand un défi est terminé (deux runs connus, ou partie synchrone terminée).
     * {@code winnerId} vaut {@code null} en cas d'égalité.
     */
    record ChallengeCompletedEvent(
            String challengeId,
            String challengerId,
            String challengedId,
            String winnerId,
            int challengerScore,
            int challengedScore,
            Instant completedAt
    ) implements ChallengeEvent {
    }
}
