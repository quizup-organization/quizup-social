package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.UserFollowerCommand;
import io.github.quizup.social.domain.model.FollowerIds;

import java.util.concurrent.CompletableFuture;

public interface FollowUserUseCase {
    CompletableFuture<String> follow(UserFollowerCommand.FollowUserCommand command);

    default CompletableFuture<String> follow(String followerId, String followedId) {
        return follow(new UserFollowerCommand.FollowUserCommand(FollowerIds.user(followerId, followedId), followerId, followedId));
    }

    default CompletableFuture<String> follow(String followId, String followerId, String followedId) {
        return follow(new UserFollowerCommand.FollowUserCommand(followId, followerId, followedId));
    }
}
