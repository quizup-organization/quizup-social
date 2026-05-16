package io.github.quizup.social.infrastructure.in.api.response;

import java.io.Serializable;
import java.time.Instant;

public record FriendRequestResponse(
        String requestId,
        String senderId,
        String targetId,
        Instant sentAt
) implements Serializable {
}
