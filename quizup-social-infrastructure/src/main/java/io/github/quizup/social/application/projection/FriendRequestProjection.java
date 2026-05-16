package io.github.quizup.social.application.projection;

import io.github.quizup.social.domain.event.FriendRequestEvent;
import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.model.FriendRequestStatus;
import io.github.quizup.social.domain.port.out.FriendRequestRepositoryPort;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * FriendshipProjection — Gère les projections pour les demandes d'ami et les amitiés.
 */
@Component
public class FriendRequestProjection {

    private static final Logger logger = LoggerFactory.getLogger(FriendRequestProjection.class);

    private final FriendRequestRepositoryPort friendRequestRepositoryPort;

    public FriendRequestProjection(FriendRequestRepositoryPort friendRequestRepositoryPort) {
        this.friendRequestRepositoryPort = friendRequestRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(FriendRequestEvent.FriendRequestSentEvent event) {
        logger.debug("FriendRequestSentEvent: requestId={}, senderId={}, targetId={}",
                event.requestId(), event.senderId(), event.targetId());

        FriendRequest friendRequest = new FriendRequest(
                event.requestId(),
                event.senderId(),
                event.targetId(),
                FriendRequestStatus.PENDING,
                event.sentAt(),
                null
        );

        friendRequestRepositoryPort.save(friendRequest);
        logger.info("Friend request created: {}", event.requestId());
    }

    @EventHandler
    @Transactional
    public void on(FriendRequestEvent.FriendRequestAcceptedEvent event) {
        logger.debug("FriendRequestAcceptedEvent: requestId={}, senderId={}, targetId={}", event.requestId(), event.senderId(), event.targetId());

        // Mettre à jour la demande d'ami
        friendRequestRepositoryPort.findById(event.requestId()).ifPresent(friendRequest ->
                friendRequestRepositoryPort.save(
                        friendRequest.toBuilder()
                                .status(FriendRequestStatus.ACCEPTED)
                                .respondedAt(event.acceptedAt())
                                .build()
                )
        );

        logger.info("Friend request accepted: {}", event.requestId());
    }

    @EventHandler
    @Transactional
    public void on(FriendRequestEvent.FriendRequestRejectedEvent event) {
        logger.debug("FriendRequestRejectedEvent: requestId={}", event.requestId());

        friendRequestRepositoryPort.findById(event.requestId()).ifPresent(friendRequest ->
                friendRequestRepositoryPort.save(
                        friendRequest.toBuilder()
                                .status(FriendRequestStatus.REJECTED)
                                .respondedAt(event.rejectedAt())
                                .build()
                )
        );;

        logger.info("Friend request rejected: {}", event.requestId());
    }


    @EventHandler
    @Transactional
    public void on(FriendRequestEvent.FriendRequestCanceledEvent event) {
        logger.debug("FriendRequestCanceledEvent: requestId={}, senderId={}, targetId={}", event.requestId(), event.senderId(), event.targetId());

        friendRequestRepositoryPort.deleteById(event.requestId());

        logger.info("Friend request canceled: {}", event.requestId());
    }
}
