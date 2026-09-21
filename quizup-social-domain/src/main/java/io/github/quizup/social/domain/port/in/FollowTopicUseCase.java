package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.TopicFollowerCommand;
import io.github.quizup.social.domain.model.FollowerIds;

import java.util.concurrent.CompletableFuture;

public interface FollowTopicUseCase {
    CompletableFuture<String> follow(TopicFollowerCommand.FollowTopicCommand command);

    default CompletableFuture<String> follow(String topicId, String userId) {
        return follow(new TopicFollowerCommand.FollowTopicCommand(FollowerIds.topic(topicId, userId), topicId, userId));
    }
    default CompletableFuture<String> follow(String followId, String topicId, String userId) {
        return follow(new TopicFollowerCommand.FollowTopicCommand(followId, topicId, userId));
    }
}

