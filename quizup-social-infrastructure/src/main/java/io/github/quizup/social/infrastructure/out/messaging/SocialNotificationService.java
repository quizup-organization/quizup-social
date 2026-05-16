package io.github.quizup.social.infrastructure.out.messaging;

import io.github.quizup.social.domain.event.FriendRequestEvent;
import io.github.quizup.social.infrastructure.out.messaging.mapper.SocialEventNotificationMapper;
import io.github.quizup.social.infrastructure.out.messaging.response.SocialNotification;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocialNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SocialNotificationService.class);
    private static final String DESTINATION_PREFIX = "/topic/social/";

    private final SimpMessagingTemplate messagingTemplate;

    public SocialNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventHandler
    public void onFriendRequestEvent(FriendRequestEvent event) {
        SocialEventNotificationMapper.toNotification(event)
                .ifPresentOrElse(
                        this::send,
                        () -> logger.warn("No notification mapping for: {}", event.getClass().getSimpleName())
                );
    }

    private void send(SocialNotification notification) {
        String destination = DESTINATION_PREFIX + notification.userId();
        logger.debug("{} published: requestId={}, userId={}", notification.type(), notification.requestId(), notification.userId());
        messagingTemplate.convertAndSend(destination, notification);
    }
}
