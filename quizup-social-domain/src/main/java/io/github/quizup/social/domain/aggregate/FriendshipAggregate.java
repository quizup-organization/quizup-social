package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.FriendshipCommand;
import io.github.quizup.social.domain.event.FriendshipEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * FriendshipAggregate — Cycle de vie d'une amitié établie.
 */
@Aggregate
public class FriendshipAggregate {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipAggregate.class);

    @AggregateIdentifier
    private String friendshipId;
    private String userId1;
    private String userId2;
    private Instant createdAt;

    protected FriendshipAggregate() {
    }

    /**
     * CommandHandler — Créer une nouvelle amitié.
     * Le friendshipId est fourni par la saga (userId1 + "-" + userId2, avec tri lexicographique).
     */
    @CommandHandler
    public FriendshipAggregate(FriendshipCommand.CreateFriendshipCommand command) {
        logger.debug("Handling CreateFriendshipCommand: friendshipId={}, userId1={}, userId2={}",
                command.friendshipId(), command.userId1(), command.userId2());

        AggregateLifecycle.apply(new FriendshipEvent.FriendshipCreatedEvent(
                command.friendshipId(),
                command.userId1(),
                command.userId2(),
                Instant.now()
        ));
    }

    /**
     * CommandHandler — Supprimer une amitié.
     */
    @CommandHandler
    public void handle(FriendshipCommand.RemoveFriendshipCommand command) {
        logger.debug("Handling RemoveFriendshipCommand: friendshipId={}", command.friendshipId());

        AggregateLifecycle.apply(new FriendshipEvent.FriendshipRemovedEvent(
                friendshipId,
                userId1,
                userId2,
                Instant.now()
        ));
    }

    // === Event Sourcing Handlers ===

    @EventSourcingHandler
    public void on(FriendshipEvent.FriendshipCreatedEvent event) {
        this.friendshipId = event.friendshipId();
        this.userId1 = event.userId1();
        this.userId2 = event.userId2();
        this.createdAt = event.createdAt();
    }

    @EventSourcingHandler
    public void on(FriendshipEvent.FriendshipRemovedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
