package io.github.quizup.social.infrastructure.in.api.response;

import io.github.quizup.social.domain.model.ChallengeStatus;

import java.time.Instant;

/**
 * Response enrichie pour un défi.
 */
public record ChallengeResponse(
        String challengeId,
        String challengerId,
        String challengedId,
        String topicId,
        ChallengeStatus status,
        Instant createdAt,
        Instant acceptedAt,
        Instant declinedAt,
        Instant expiresAt
) {
}
