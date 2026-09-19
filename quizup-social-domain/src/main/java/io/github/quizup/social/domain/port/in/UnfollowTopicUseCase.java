package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.TopicFollowerCommand;

import java.util.concurrent.CompletableFuture;

public interface UnfollowTopicUseCase {
    CompletableFuture<String> unfollow(TopicFollowerCommand.UnfollowTopicCommand command);

    default CompletableFuture<String> unfollow(String followId) {
        return unfollow(new TopicFollowerCommand.UnfollowTopicCommand(followId));
    }

    default void unfollowAndWait(String followId) {
        unfollow(followId).join();
    }
}

