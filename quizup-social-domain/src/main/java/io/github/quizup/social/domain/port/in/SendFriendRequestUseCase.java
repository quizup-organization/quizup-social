package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.FriendRequestCommand;

import java.util.concurrent.CompletableFuture;

public interface SendFriendRequestUseCase {
    CompletableFuture<String> send(FriendRequestCommand.SendFriendRequestCommand command);

    default CompletableFuture<String> send(String requestId, String senderId, String targetId) {
        return send(new FriendRequestCommand.SendFriendRequestCommand(requestId, senderId, targetId));
    }

    default void sendAndWait(String requestId, String senderId, String targetId) {
        send(requestId, senderId, targetId).join();
    }
}

