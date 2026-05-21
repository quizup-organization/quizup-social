package io.github.quizup.social.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface TopicFollowerCommand {

    String followId();

    record FollowTopicCommand(
            @TargetAggregateIdentifier String followId,
            String topicId,
            String userId
    ) implements TopicFollowerCommand {
    }

    record UnfollowTopicCommand(
            @TargetAggregateIdentifier String followId
    ) implements TopicFollowerCommand {
    }
}

