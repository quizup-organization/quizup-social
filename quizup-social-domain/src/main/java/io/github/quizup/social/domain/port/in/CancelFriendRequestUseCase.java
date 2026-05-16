package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.FriendRequestCommand;

import java.util.concurrent.CompletableFuture;

public interface CancelFriendRequestUseCase {
    CompletableFuture<String> cancel(FriendRequestCommand.CancelFriendRequestCommand command);

    default CompletableFuture<String> cancel(String requestId) {
        return cancel(new FriendRequestCommand.CancelFriendRequestCommand(requestId));
    }

    default void cancelAndWait(String requestId) {
        cancel(requestId).join();
    }
}

