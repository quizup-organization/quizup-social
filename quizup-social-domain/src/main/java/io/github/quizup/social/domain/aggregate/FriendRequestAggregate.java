package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.FriendRequestCommand;
import io.github.quizup.social.domain.event.FriendRequestEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.model.FriendRequestStatus;
import io.github.quizup.social.domain.port.out.FriendshipRepositoryPort;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * FriendRequestAggregate — Cycle de vie d'une demande d'ami.
 * ID = requestId (UUID généré par le controller avant d'envoyer la commande).
 * <p>
 * States: PENDING → ACCEPTED | REJECTED
 */
@Aggregate
public class FriendRequestAggregate {

    private static final Logger logger = LoggerFactory.getLogger(FriendRequestAggregate.class);

    @AggregateIdentifier
    private String requestId;
    private String senderId;
    private String targetId;
    private FriendRequestStatus status; // PENDING | ACCEPTED

    protected FriendRequestAggregate() {
    }

    /**
     * CommandHandler — Créer une nouvelle demande d'ami.
     * Le requestId est fourni par le controller (UUID.randomUUID()).
     */


    @CommandHandler
    public FriendRequestAggregate(FriendRequestCommand.SendFriendRequestCommand command,
                                  FriendshipRepositoryPort friendshipRepositoryPort) {
        logger.debug("Handling SendFriendRequestCommand: requestId={}, senderId={}, targetId={}",
                command.requestId(), command.senderId(), command.targetId());

        if (command.senderId().equals(command.targetId())) {
            throw new SocialExceptions.CannotSendRequestToSelfProblem(command.senderId());
        }

        boolean friendshipExists = friendshipRepositoryPort.existsBetweenUsers(command.senderId(), command.targetId());

        if (friendshipExists) {
            throw new SocialExceptions.FriendShipAlreadyExistsProblem(command.senderId(), command.targetId());
        }

        AggregateLifecycle.apply(
                new FriendRequestEvent.FriendRequestSentEvent(
                        command.requestId(),
                        command.senderId(),
                        command.targetId(),
                        Instant.now()
                )
        );
    }

    /**
     * CommandHandler — Accepter une demande d'ami.
     */
    @CommandHandler
    public void handle(FriendRequestCommand.AcceptFriendRequestCommand command) {
        logger.debug("Handling AcceptFriendRequestCommand: requestId={}", command.requestId());

        if (!FriendRequestStatus.PENDING.equals(status)) {
            throw new SocialExceptions.FriendRequestNotPendingProblem(requestId, status);
        }

        AggregateLifecycle.apply(
                new FriendRequestEvent.FriendRequestAcceptedEvent(
                        requestId,
                        senderId,
                        targetId,
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(FriendRequestCommand.CancelFriendRequestCommand command) {
        logger.debug("Handling CancelFriendRequestCommand: requestId={}", command.requestId());

        if (!FriendRequestStatus.PENDING.equals(status)) {
            throw new SocialExceptions.FriendRequestNotPendingProblem(requestId, status);
        }

        AggregateLifecycle.apply(
                new FriendRequestEvent.FriendRequestCanceledEvent(
                        requestId,
                        senderId,
                        targetId,
                        Instant.now()
                )
        );
    }

    /**
     * CommandHandler — Rejeter une demande d'ami.
     */
    @CommandHandler
    public void handle(FriendRequestCommand.RejectFriendRequestCommand command) {
        logger.debug("Handling RejectFriendRequestCommand: requestId={}", command.requestId());

        if (!FriendRequestStatus.PENDING.equals(status)) {
            throw new SocialExceptions.FriendRequestNotPendingProblem(requestId, status);
        }

        AggregateLifecycle.apply(
                new FriendRequestEvent.FriendRequestRejectedEvent(
                        requestId,
                        senderId,
                        targetId,
                        Instant.now()
                )
        );
    }

    // === Event Sourcing Handlers ===

    @EventSourcingHandler
    public void on(FriendRequestEvent.FriendRequestSentEvent event) {
        this.requestId = event.requestId();
        this.senderId = event.senderId();
        this.targetId = event.targetId();
        this.status = FriendRequestStatus.PENDING;
    }

    @EventSourcingHandler
    public void on(FriendRequestEvent.FriendRequestAcceptedEvent event) {
        this.status = FriendRequestStatus.ACCEPTED;
    }

    @EventSourcingHandler
    public void on(FriendRequestEvent.FriendRequestRejectedEvent event) {
        this.status = FriendRequestStatus.REJECTED;
    }

    @EventSourcingHandler
    public void on(FriendRequestEvent.FriendRequestCanceledEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
