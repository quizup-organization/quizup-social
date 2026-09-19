package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record UserFollower(
        String followId,
        String followerId,
        String followedId,
        Instant followedAt
) {
}
