package io.github.quizup.social.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface UserFollowerCommand {

    String followId();

    record FollowUserCommand(
            @TargetAggregateIdentifier String followId,
            String followerId,
            String followedId
    ) implements UserFollowerCommand {
    }

    record UnfollowUserCommand(
            @TargetAggregateIdentifier String followId
    ) implements UserFollowerCommand {
    }
}
