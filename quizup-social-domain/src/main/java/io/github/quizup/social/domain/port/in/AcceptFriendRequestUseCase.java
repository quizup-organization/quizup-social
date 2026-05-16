package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.FriendRequestCommand;

import java.util.concurrent.CompletableFuture;

public interface AcceptFriendRequestUseCase {
    CompletableFuture<String> accept(FriendRequestCommand.AcceptFriendRequestCommand command);

    default CompletableFuture<String> accept(String requestId) {
        return accept(new FriendRequestCommand.AcceptFriendRequestCommand(requestId));
    }

    default void acceptAndWait(String requestId) {
        accept(requestId).join();
    }
}

