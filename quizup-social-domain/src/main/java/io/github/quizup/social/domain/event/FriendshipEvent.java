package io.github.quizup.social.domain.event;

import java.time.Instant;

public interface FriendshipEvent {

    String friendshipId();

    record FriendshipCreatedEvent(
            String friendshipId,
            String userId1,
            String userId2,
            Instant createdAt
    ) implements FriendshipEvent {
    }

    record FriendshipRemovedEvent(
            String friendshipId,
            String userId1,
            String userId2,
            Instant removedAt
    ) implements FriendshipEvent {
    }
}

