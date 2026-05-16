package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record FriendRequest(
        String requestId,
        String senderId,
        String targetId,
        FriendRequestStatus status,
        Instant sentAt,
        Instant respondedAt
) {
}

