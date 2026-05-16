package io.github.quizup.social.application.projection;

import io.github.quizup.social.domain.event.FriendshipEvent;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.domain.port.out.FriendshipRepositoryPort;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * FriendshipProjection — Gère les projections pour les demandes d'ami et les amitiés.
 */
@Component
public class FriendshipProjection {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipProjection.class);

    private final FriendshipRepositoryPort friendshipRepositoryPort;

    public FriendshipProjection(FriendshipRepositoryPort friendshipRepositoryPort) {
        this.friendshipRepositoryPort = friendshipRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(FriendshipEvent.FriendshipCreatedEvent event) {
        logger.debug("FriendshipCreatedEvent: friendshipId={}, userId1={}, userId2={}", event.friendshipId(), event.userId1(), event.userId2());

        Friendship friendship = new Friendship(
                event.friendshipId(),
                event.userId1(),
                event.userId2(),
                event.createdAt()
        );

        friendshipRepositoryPort.save(friendship);

        logger.info("Friendship created: friendshipId={}", event.friendshipId());
    }

    @EventHandler
    @Transactional
    public void on(FriendshipEvent.FriendshipRemovedEvent event) {
        logger.debug("FriendshipRemovedEvent: friendshipId={}, userId1={}, userId2={}", event.friendshipId(), event.userId1(), event.userId2());

        friendshipRepositoryPort.deleteById(event.friendshipId());

        logger.info("Friendship removed: friendshipId={}", event.friendshipId());
    }
}
