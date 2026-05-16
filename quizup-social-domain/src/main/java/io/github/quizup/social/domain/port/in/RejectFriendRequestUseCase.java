package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.FriendRequestCommand;

import java.util.concurrent.CompletableFuture;

public interface RejectFriendRequestUseCase {
    CompletableFuture<String> reject(FriendRequestCommand.RejectFriendRequestCommand command);

    default CompletableFuture<String> reject(String requestId) {
        return reject(new FriendRequestCommand.RejectFriendRequestCommand(requestId));
    }

    default void rejectAndWait(String requestId) {
        reject(requestId).join();
    }
}

