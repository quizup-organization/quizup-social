package io.github.quizup.social.infrastructure.out.messaging.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SocialNotification.FriendRequestReceivedNotification.class, name = "FRIEND_REQUEST_RECEIVED"),
        @JsonSubTypes.Type(value = SocialNotification.FriendRequestAcceptedNotification.class, name = "FRIEND_REQUEST_ACCEPTED"),
        @JsonSubTypes.Type(value = SocialNotification.FriendRequestRejectedNotification.class, name = "FRIEND_REQUEST_REJECTED")
})
public interface SocialNotification {

    SocialNotificationType type();

    String requestId();

    /** Identifiant de l'utilisateur destinataire — utilisé pour router vers /topic/social/{userId} */
    String userId();

    enum SocialNotificationType {
        FRIEND_REQUEST_RECEIVED,
        FRIEND_REQUEST_ACCEPTED,
        FRIEND_REQUEST_REJECTED
    }

    record FriendRequestReceivedNotification(
            String requestId,
            String senderId,
            String userId,   // targetId : celui qui reçoit la demande
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
            String userId,   // senderId : celui dont la demande a été acceptée
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
            String userId,   // senderId : celui dont la demande a été rejetée
            String timestamp
    ) implements SocialNotification {
        @Override
        public SocialNotificationType type() {
            return SocialNotificationType.FRIEND_REQUEST_REJECTED;
        }
    }
}
