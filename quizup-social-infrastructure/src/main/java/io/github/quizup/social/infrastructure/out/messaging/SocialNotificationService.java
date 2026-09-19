package io.github.quizup.social.infrastructure.out.messaging;

import io.github.quizup.microservice.core.domain.model.notification.NotificationEnvelope;
import io.github.quizup.social.domain.event.ChallengeEvent;
import io.github.quizup.social.infrastructure.out.messaging.mapper.SocialEventNotificationMapper;
import io.github.quizup.social.infrastructure.out.messaging.response.SocialNotification;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Pousse les notifications sociales (défis) enrichies de leurs métadonnées d'ordre, même contrat
 * que les notifications de partie/lobby.
 */
@Service
public class SocialNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SocialNotificationService.class);
    private static final String DESTINATION_PREFIX = "/topic/social/";

    private final SimpMessagingTemplate messagingTemplate;

    public SocialNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventHandler
    public void onChallengeEvent(EventMessage<?> eventMessage) {
        if (!(eventMessage.getPayload() instanceof ChallengeEvent event)) {
            return;
        }
        SocialEventNotificationMapper.toNotification(event)
                .ifPresentOrElse(
                        notification -> send(event, eventMessage, notification),
                        () -> logger.warn("No notification mapping for: {}", event.getClass().getSimpleName())
                );
    }

    private void send(ChallengeEvent event, EventMessage<?> eventMessage, SocialNotification notification) {
        if (!(eventMessage instanceof DomainEventMessage<?> domainMessage)) {
            logger.warn("Notification ignorée (événement sans métadonnées d'agrégat): {}", event.getClass().getSimpleName());
            return;
        }

        NotificationEnvelope<SocialNotification> envelope = new NotificationEnvelope<>(
                domainMessage.getIdentifier(),
                event.challengeId(),
                domainMessage.getSequenceNumber(),
                domainMessage.getTimestamp(),
                notification
        );

        String destination = DESTINATION_PREFIX + notification.userId();
        logger.debug("{} published: userId={}, seq={}", notification.type(), notification.userId(), domainMessage.getSequenceNumber());
        messagingTemplate.convertAndSend(destination, envelope);
    }
}
