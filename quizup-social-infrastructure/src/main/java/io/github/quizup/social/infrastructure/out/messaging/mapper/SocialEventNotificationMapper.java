package io.github.quizup.social.infrastructure.out.messaging.mapper;

import io.github.quizup.social.domain.event.ChallengeEvent;
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

    public static Optional<SocialNotification> toNotification(ChallengeEvent event) {
        if (isNull(event)) return Optional.empty();

        return switch (event) {
            case ChallengeEvent.ChallengeCreatedEvent e -> Optional.of(
                    new SocialNotification.ChallengeReceivedNotification(
                            e.challengeId(),
                            e.challengerId(),
                            e.topicId(),
                            e.challengedId(),
                            e.expiresAt().toString()
                    )
            );
            case ChallengeEvent.ChallengeAcceptedEvent e -> Optional.of(
                    new SocialNotification.ChallengeAcceptedNotification(
                            e.challengeId(),
                            e.gameId(),
                            e.challengedId(),
                            e.challengerId(),
                            e.acceptedAt().toString()
                    )
            );
            case ChallengeEvent.ChallengeDeclinedEvent e -> Optional.of(
                    new SocialNotification.ChallengeDeclinedNotification(
                            e.challengeId(),
                            e.challengedId(),
                            e.challengerId(),
                            e.declinedAt().toString()
                    )
            );
            case ChallengeEvent.ChallengeExpiredEvent e -> Optional.of(
                    new SocialNotification.ChallengeExpiredNotification(
                            e.challengeId(),
                            e.challengerId(),
                            e.expiredAt().toString()
                    )
            );
            default -> Optional.empty();
        };
    }
}
