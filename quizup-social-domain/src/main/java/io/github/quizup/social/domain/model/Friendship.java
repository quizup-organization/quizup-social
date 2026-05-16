package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Friendship(
        String friendshipId,
        String userId1,
        String userId2,
        Instant friendsSince
) {
}

