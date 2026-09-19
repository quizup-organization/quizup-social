package io.github.quizup.social.infrastructure.in.api.response;

import java.io.Serializable;
import java.time.Instant;

public record UserFollowerResponse(
        String followId,
        String followerId,
        String followedId,
        Instant followedAt
) implements Serializable {
}
