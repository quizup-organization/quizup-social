package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.UserFollowerCommand;
import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

@Getter
@Aggregate
public class UserFollowerAggregate {

    @AggregateIdentifier
    private String followId;
    private String followerId;
    private String followedId;
    private Instant followedAt;

    protected UserFollowerAggregate() {
    }

    @CommandHandler
    public UserFollowerAggregate(
            UserFollowerCommand.FollowUserCommand command,
            ProfileRepositoryPort profileRepositoryPort,
            UserFollowerRepositoryPort userFollowerRepositoryPort
    ) {
        if (!profileRepositoryPort.existsById(command.followerId())) {
            throw new SocialExceptions.UserNotFoundProblem(command.followerId());
        }

        if (!profileRepositoryPort.existsById(command.followedId())) {
            throw new SocialExceptions.UserNotFoundProblem(command.followedId());
        }

        if (command.followerId().equals(command.followedId())) {
            throw new SocialExceptions.CannotFollowSelfProblem(command.followerId());
        }

        if (userFollowerRepositoryPort.exists(command.followerId(), command.followedId())) {
            throw new SocialExceptions.UserAlreadyFollowedProblem(command.followerId(), command.followedId());
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

    @CommandHandler
    public void handle(UserFollowerCommand.UnfollowUserCommand command) {
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
    }

    @EventSourcingHandler
    public void on(UserFollowerEvent.UserUnfollowedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
