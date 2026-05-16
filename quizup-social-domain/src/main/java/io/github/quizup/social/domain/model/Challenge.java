package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.time.Instant;

/**
 * Challenge domain model — représentation immuable d'un défi
 */
@Builder(toBuilder = true)
public record Challenge(
        String challengeId,
        String challengerId,
        String challengedId,
        String topicId,
        String gameId,
        ChallengeStatus status,
        Instant createdAt,
        Instant acceptedAt,
        Instant declinedAt,
        Instant expiresAt
) {
}

