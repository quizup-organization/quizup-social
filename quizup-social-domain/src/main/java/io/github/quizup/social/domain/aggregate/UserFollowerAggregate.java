package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.UserFollowerCommand;
import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

/**
 * Suivi unidirectionnel d'un joueur, identifié par {@code followerId:followedId} (id déterministe).
 *
 * <p>Un <b>seul</b> handler de commande ({@link CreationPolicy} {@code CREATE_IF_MISSING}) gère
 * follow et re-suivi : pas d'ambiguïté constructeur/instance. L'agrégat est <b>non destructif</b>
 * (le désabonnement repasse {@code followed = false}), ce qui rend l'unicité race-free (Axon
 * sérialise les commandes par id d'agrégat) et le re-suivi idempotent.</p>
 */
@Getter
@Aggregate
public class UserFollowerAggregate {

    @AggregateIdentifier
    private String followId;
    private String followerId;
    private String followedId;
    private Instant followedAt;
    private boolean followed;

    protected UserFollowerAggregate() {
    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(
            UserFollowerCommand.FollowUserCommand command,
            ProfileRepositoryPort profileRepositoryPort
    ) {
        if (followed) {
            return;
        }
        if (command.followerId().equals(command.followedId())) {
            throw new SocialExceptions.CannotFollowSelfProblem(command.followerId());
        }
        if (!profileRepositoryPort.existsById(command.followedId())) {
            throw new SocialExceptions.UserNotFoundProblem(command.followedId());
        }
        AggregateLifecycle.apply(
                new UserFollowerEvent.UserFollowedEvent(
                        command.followId(),
                        command.followerId(),
                        command.followedId(),
                        Instant.now()
                )
        );
    }

    /** Désabonnement idempotent (no-op si déjà désabonné). */
    @CommandHandler
    public void handle(UserFollowerCommand.UnfollowUserCommand command) {
        if (!followed) {
            return;
        }
        AggregateLifecycle.apply(
                new UserFollowerEvent.UserUnfollowedEvent(
                        this.followId,
                        this.followerId,
                        this.followedId,
                        Instant.now()
                )
        );
    }

    @EventSourcingHandler
    public void on(UserFollowerEvent.UserFollowedEvent event) {
        this.followId = event.followId();
        this.followerId = event.followerId();
        this.followedId = event.followedId();
        this.followedAt = event.followedAt();
        this.followed = true;
    }

    @EventSourcingHandler
    public void on(UserFollowerEvent.UserUnfollowedEvent event) {
        this.followed = false;
    }
}
