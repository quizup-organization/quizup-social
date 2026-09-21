package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.TopicFollowerCommand;
import io.github.quizup.social.domain.event.TopicFollowerEvent;
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
 * Suivi d'un sujet, identifié par {@code userId:topicId} (id déterministe).
 *
 * <p>Un <b>seul</b> handler de commande ({@link CreationPolicy} {@code CREATE_IF_MISSING}) gère
 * follow et re-suivi : pas d'ambiguïté constructeur/instance. L'agrégat est <b>non destructif</b>
 * (le désabonnement repasse {@code followed = false}), ce qui rend l'unicité race-free (Axon
 * sérialise les commandes par id d'agrégat) et le re-suivi idempotent.</p>
 *
 * <p><b>Aucune dépendance externe</b> : ni le topic ni l'utilisateur (authentifié) ne sont
 * validés ici. Le topic est fourni par le client (et ignoré par `theme` s'il est inconnu) ; le
 * `userId` vient du JWT.</p>
 */
@Getter
@Aggregate
public class TopicFollowerAggregate {

    @AggregateIdentifier
    private String followId;
    private String topicId;
    private String userId;
    private Instant followedAt;
    private boolean followed;

    protected TopicFollowerAggregate() {
    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(TopicFollowerCommand.FollowTopicCommand command) {
        if (followed) {
            return;
        }
        AggregateLifecycle.apply(
                new TopicFollowerEvent.TopicFollowedEvent(
                        command.followId(),
                        command.topicId(),
                        command.userId(),
                        Instant.now()
                )
        );
    }

    /** Désabonnement idempotent (no-op si déjà désabonné). */
    @CommandHandler
    public void handle(TopicFollowerCommand.UnfollowTopicCommand command) {
        if (!followed) {
            return;
        }
        AggregateLifecycle.apply(
                new TopicFollowerEvent.TopicUnfollowedEvent(
                        this.followId,
                        this.topicId,
                        this.userId,
                        Instant.now()
                )
        );
    }

    @EventSourcingHandler
    public void on(TopicFollowerEvent.TopicFollowedEvent event) {
        this.followId = event.followId();
        this.topicId = event.topicId();
        this.userId = event.userId();
        this.followedAt = event.followedAt();
        this.followed = true;
    }

    @EventSourcingHandler
    public void on(TopicFollowerEvent.TopicUnfollowedEvent event) {
        this.followed = false;
    }
}
