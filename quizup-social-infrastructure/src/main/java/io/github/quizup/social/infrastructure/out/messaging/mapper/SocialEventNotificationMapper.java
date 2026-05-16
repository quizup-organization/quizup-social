package io.github.quizup.social.infrastructure.out.messaging.mapper;

import io.github.quizup.social.domain.event.FriendRequestEvent;
import io.github.quizup.social.infrastructure.out.messaging.response.SocialNotification;

import java.util.Optional;

import static java.util.Objects.isNull;

public final class SocialEventNotificationMapper {

    private SocialEventNotificationMapper() {
    }

    public static Optional<SocialNotification> toNotification(FriendRequestEvent event) {
        if (isNull(event)) return Optional.empty();

        return switch (event) {
            case FriendRequestEvent.FriendRequestSentEvent e -> Optional.of(
                    new SocialNotification.FriendRequestReceivedNotification(
                            e.requestId(),
                            e.senderId(),
                            e.targetId(),          // destinataire : celui qui reçoit la demande
                            e.sentAt().toString()
                    )
            );
            case FriendRequestEvent.FriendRequestAcceptedEvent e -> Optional.of(
                    new SocialNotification.FriendRequestAcceptedNotification(
                            e.requestId(),
                            e.targetId(),          // acceptedBy = targetId (celui qui a accepté)
                            e.senderId(),          // destinataire : l'initiateur de la demande
                            e.acceptedAt().toString()
                    )
            );
            case FriendRequestEvent.FriendRequestRejectedEvent e -> Optional.of(
                    new SocialNotification.FriendRequestRejectedNotification(
                            e.requestId(),
                            e.targetId(),          // rejectedBy = targetId (celui qui a rejeté)
                            e.senderId(),          // destinataire : l'initiateur de la demande
                            e.rejectedAt().toString()
                    )
            );
            default -> Optional.empty();           // FriendRequestCanceledEvent : pas de notification
        };
    }
}
