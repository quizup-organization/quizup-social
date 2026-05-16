package io.github.quizup.social.infrastructure.out.messaging.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SocialNotification.FriendRequestReceivedNotification.class, name = "FRIEND_REQUEST_RECEIVED"),
        @JsonSubTypes.Type(value = SocialNotification.FriendRequestAcceptedNotification.class, name = "FRIEND_REQUEST_ACCEPTED"),
        @JsonSubTypes.Type(value = SocialNotification.FriendRequestRejectedNotification.class, name = "FRIEND_REQUEST_REJECTED"),
        @JsonSubTypes.Type(value = SocialNotification.ChallengeReceivedNotification.class, name = "CHALLENGE_RECEIVED"),
        @JsonSubTypes.Type(value = SocialNotification.ChallengeAcceptedNotification.class, name = "CHALLENGE_ACCEPTED"),
        @JsonSubTypes.Type(value = SocialNotification.ChallengeDeclinedNotification.class, name = "CHALLENGE_DECLINED"),
        @JsonSubTypes.Type(value = SocialNotification.ChallengeExpiredNotification.class, name = "CHALLENGE_EXPIRED")
})
public interface SocialNotification {

    SocialNotificationType type();

    String userId();

    enum SocialNotificationType {
        FRIEND_REQUEST_RECEIVED,
        FRIEND_REQUEST_ACCEPTED,
        FRIEND_REQUEST_REJECTED,
        CHALLENGE_RECEIVED,
        CHALLENGE_ACCEPTED,
        CHALLENGE_DECLINED,
        CHALLENGE_EXPIRED
    }

    record FriendRequestReceivedNotification(
            String requestId,
            String senderId,
            String userId,
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.FRIEND_REQUEST_RECEIVED;
        }
    }

    record FriendRequestAcceptedNotification(
            String requestId,
            String acceptedBy,
            String userId,
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.FRIEND_REQUEST_ACCEPTED;
        }
    }

    record FriendRequestRejectedNotification(
            String requestId,
            String rejectedBy,
            String userId,
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.FRIEND_REQUEST_REJECTED;
        }
    }

    record ChallengeReceivedNotification(
            String challengeId,
            String challengerId,
            String topicId,
            String userId,
            String expiresAt
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.CHALLENGE_RECEIVED;
        }
    }

    record ChallengeAcceptedNotification(
            String challengeId,
            String gameId,
            String acceptedBy,
            String userId,
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.CHALLENGE_ACCEPTED;
        }
    }

    record ChallengeDeclinedNotification(
            String challengeId,
            String declinedBy,
            String userId,
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.CHALLENGE_DECLINED;
        }
    }

    record ChallengeExpiredNotification(
            String challengeId,
            String userId,
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.CHALLENGE_EXPIRED;
        }
    }
}
