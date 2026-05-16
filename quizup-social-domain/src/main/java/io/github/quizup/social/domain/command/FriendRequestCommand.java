package io.github.quizup.social.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface FriendRequestCommand {

    String requestId();

    record SendFriendRequestCommand(
            @TargetAggregateIdentifier String requestId,
            String senderId,
            String targetId
    ) implements FriendRequestCommand {
    }

    record AcceptFriendRequestCommand(
            @TargetAggregateIdentifier String requestId
    ) implements FriendRequestCommand {
    }

    record RejectFriendRequestCommand(
            @TargetAggregateIdentifier String requestId
    ) implements FriendRequestCommand {
    }

    record CancelFriendRequestCommand(
            @TargetAggregateIdentifier String requestId
    ) implements FriendRequestCommand {
    }
}

