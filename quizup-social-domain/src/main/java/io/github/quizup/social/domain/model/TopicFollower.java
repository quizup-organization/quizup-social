package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record TopicFollower(
        String followId,
        String topicId,
        String userId,
        Instant followedAt,
        Instant unfollowedAt
) {
}

