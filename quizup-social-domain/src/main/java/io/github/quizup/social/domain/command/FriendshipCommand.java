package io.github.quizup.social.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface FriendshipCommand {

    String friendshipId();

    record CreateFriendshipCommand(
            @TargetAggregateIdentifier String friendshipId,
            String userId1,
            String userId2
    ) implements FriendshipCommand {
    }

    record RemoveFriendshipCommand(
            @TargetAggregateIdentifier String friendshipId
    ) implements FriendshipCommand {
    }
}

