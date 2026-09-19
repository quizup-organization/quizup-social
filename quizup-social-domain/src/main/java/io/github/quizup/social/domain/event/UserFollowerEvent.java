package io.github.quizup.social.domain.event;

import java.time.Instant;

public interface UserFollowerEvent {

    String followId();

    String followerId();

    String followedId();

    record UserFollowedEvent(
            String followId,
            String followerId,
            String followedId,
            Instant followedAt
    ) implements UserFollowerEvent {
    }

    record UserUnfollowedEvent(
            String followId,
            String followerId,
            String followedId,
            Instant unfollowedAt
    ) implements UserFollowerEvent {
    }
}
