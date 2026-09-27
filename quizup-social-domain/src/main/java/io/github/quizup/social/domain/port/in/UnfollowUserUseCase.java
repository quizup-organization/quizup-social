package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.UserFollowerCommand;

import java.util.concurrent.CompletableFuture;

public interface UnfollowUserUseCase {
    CompletableFuture<String> unfollow(UserFollowerCommand.UnfollowUserCommand command);

    default CompletableFuture<String> unfollow(String followId, String actorId) {
        return unfollow(new UserFollowerCommand.UnfollowUserCommand(followId, actorId));
    }
}
