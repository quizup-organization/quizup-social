package io.github.quizup.social.infrastructure.in.api.response;

import java.io.Serializable;
import java.time.Instant;

public record FriendshipResponse(
        String friendshipId,
        String userId1,
        String userId2,
        Instant friendsSince
) implements Serializable {
}
