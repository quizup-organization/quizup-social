package io.github.quizup.social.domain.event;

import java.time.Instant;

public interface FriendRequestEvent {

    String requestId();

    record FriendRequestSentEvent(
            String requestId,
            String senderId,
            String targetId,
            Instant sentAt
    ) implements FriendRequestEvent {
    }

    record FriendRequestAcceptedEvent(
            String requestId,
            String senderId,
            String targetId,
            Instant acceptedAt
    ) implements FriendRequestEvent {
    }

    record FriendRequestRejectedEvent(
            String requestId,
            String senderId,
            String targetId,
            Instant rejectedAt
    ) implements FriendRequestEvent {
    }

    record FriendRequestCanceledEvent(
            String requestId,
            String senderId,
            String targetId,
            Instant canceledAt
    ) implements FriendRequestEvent {
    }
}

