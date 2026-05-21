package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.TopicFollowerCommand;

import java.util.concurrent.CompletableFuture;

public interface FollowTopicUseCase {
    CompletableFuture<String> follow(TopicFollowerCommand.FollowTopicCommand command);

    default CompletableFuture<String> follow(String followId, String topicId, String userId) {
        return follow(new TopicFollowerCommand.FollowTopicCommand(followId, topicId, userId));
    }

    default void followAndWait(String followId, String topicId, String userId) {
        follow(followId, topicId, userId).join();
    }
}

