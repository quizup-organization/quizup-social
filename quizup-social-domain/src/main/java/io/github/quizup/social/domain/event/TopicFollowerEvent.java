package io.github.quizup.social.domain.event;

import java.time.Instant;

public interface TopicFollowerEvent {

    String followId();

    String topicId();

    String userId();

    record TopicFollowedEvent(
            String followId,
            String topicId,
            String userId,
            Instant followedAt
    ) implements TopicFollowerEvent {
    }

    record TopicUnfollowedEvent(
            String followId,
            String topicId,
            String userId,
            Instant unfollowedAt
    ) implements TopicFollowerEvent {
    }
}

