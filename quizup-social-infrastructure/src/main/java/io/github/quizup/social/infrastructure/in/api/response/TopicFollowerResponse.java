package io.github.quizup.social.infrastructure.in.api.response;

import java.io.Serializable;
import java.time.Instant;

public record TopicFollowerResponse(
        String followId,
        String topicId,
        String userId,
        Instant followedAt,
        Instant unfollowedAt
) implements Serializable {
}

