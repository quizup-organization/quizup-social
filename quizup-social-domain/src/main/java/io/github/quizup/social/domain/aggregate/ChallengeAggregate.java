package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.*;
import io.github.quizup.social.domain.event.*;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.ChallengeDeadline;
import io.github.quizup.social.domain.model.ChallengeStatus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

/**
 * ChallengeAggregate — Cycle de vie d'un défi.
 * <p>
 * Flux simplifié :
 * - CreateChallenge → PENDING (orchestration délai déléguée à la saga)
 * - AcceptChallenge → ACCEPTED (orchestration CreateGame déléguée à la saga)
 * - DeclineChallenge → DECLINED (rien à annuler)
 * - ExpireChallenge → EXPIRED (rien à annuler)
 */
@Aggregate
public class ChallengeAggregate {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeAggregate.class);

    @AggregateIdentifier
    private String challengeId;
    private String challengerId;
    private String challengedId;
    private String topicId;
    private ChallengeStatus status;
    private Instant createdAt;
    private Instant acceptedAt;
    private Instant declinedAt;
    private Instant expiresAt;

    protected ChallengeAggregate() {
    }

    @CommandHandler
    public ChallengeAggregate(ChallengeCommand.CreateChallengeCommand command) {
        logger.debug("Handling CreateChallengeCommand: challengeId={}, challengerId={}, challengedId={}",
                command.challengeId(), command.challengerId(), command.challengedId());

        if (command.challengerId().equals(command.challengedId())) {
            throw new ChallengeExceptions.CannotChallengeSelfProblem(command.challengedId());
        }

        Instant now = Instant.now();

        Instant expiration = now.plus(ChallengeDeadline.CHALLENGE_EXPIRED_TIMEOUT);

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeCreatedEvent(
                        command.challengeId(),
                        command.challengerId(),
                        command.challengedId(),
                        command.topicId(),
                        now,
                        expiration
                )
        );

    }

    /**
     * Accepter un défi → créer la partie et la démarrer.
     */
    @CommandHandler
    public void handle(ChallengeCommand.AcceptChallengeCommand command) {
        logger.debug("Handling AcceptChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }

        String gameId = UUID.randomUUID().toString();

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeAcceptedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        gameId,
                        Instant.now()
                )
        );

    }

    @CommandHandler
    public void handle(ChallengeCommand.DeclineChallengeCommand command) {
        logger.debug("Handling DeclineChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeDeclinedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(ChallengeCommand.ExpireChallengeCommand command) {
        logger.debug("Handling ExpireChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            return;
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeExpiredEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        Instant.now()
                )
        );
    }


    // === Event Sourcing Handlers ===

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeCreatedEvent event) {
        this.challengeId = event.challengeId();
        this.challengerId = event.challengerId();
        this.challengedId = event.challengedId();
        this.topicId = event.topicId();
        this.status = ChallengeStatus.PENDING;
        this.createdAt = event.createdAt();
        this.expiresAt = event.expiresAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeAcceptedEvent event) {
        this.status = ChallengeStatus.ACCEPTED;
        this.acceptedAt = event.acceptedAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeDeclinedEvent event) {
        this.status = ChallengeStatus.DECLINED;
        this.declinedAt = event.declinedAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeExpiredEvent event) {
        this.status = ChallengeStatus.EXPIRED;
        this.expiresAt = event.expiredAt();
    }
}
