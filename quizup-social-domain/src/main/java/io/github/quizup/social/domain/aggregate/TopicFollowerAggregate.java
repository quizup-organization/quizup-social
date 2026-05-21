package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.TopicFollowerCommand;
import io.github.quizup.social.domain.event.TopicFollowerEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.port.out.TopicFollowerRepositoryPort;
import io.github.quizup.social.domain.port.out.TopicPort;
import io.github.quizup.social.domain.port.out.UserPort;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

@Aggregate
public class TopicFollowerAggregate {

    @AggregateIdentifier
    private String followId;
    private String topicId;
    private String userId;
    private Instant followedAt;
    private Instant unfollowedAt;

    protected TopicFollowerAggregate() {
    }

    @CommandHandler
    public TopicFollowerAggregate(
            TopicFollowerCommand.FollowTopicCommand command,
            TopicPort topicPort,
            UserPort userPort,
            TopicFollowerRepositoryPort topicFollowerRepositoryPort
    ) {
        if (!topicPort.existsById(command.topicId())) {
            throw new SocialExceptions.TopicNotFoundProblem(command.topicId());
        }

        if (!userPort.existsById(command.userId())) {
            throw new SocialExceptions.UserNotFoundProblem(command.userId());
        }

        if (topicFollowerRepositoryPort.existsByTopicIdAndUserId(command.topicId(), command.userId())) {
            throw new SocialExceptions.TopicAlreadyFollowedProblem(command.topicId(), command.userId());
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

    @CommandHandler
    public void handle(TopicFollowerCommand.UnfollowTopicCommand command) {
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
        this.unfollowedAt = null;
    }

    @EventSourcingHandler
    public void on(TopicFollowerEvent.TopicUnfollowedEvent event) {
        this.unfollowedAt = event.unfollowedAt();
        AggregateLifecycle.markDeleted();
    }
}

