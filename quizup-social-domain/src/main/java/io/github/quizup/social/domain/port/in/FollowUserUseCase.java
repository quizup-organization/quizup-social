package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.UserFollowerCommand;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FollowUserUseCase {
    CompletableFuture<String> follow(UserFollowerCommand.FollowUserCommand command);

    default CompletableFuture<String> follow(String followerId, String followedId) {
        return follow(new UserFollowerCommand.FollowUserCommand(UUID.randomUUID().toString(), followerId, followedId));
    }

    default CompletableFuture<String> follow(String followId, String followerId, String followedId) {
        return follow(new UserFollowerCommand.FollowUserCommand(followId, followerId, followedId));
    }
}
