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
}
