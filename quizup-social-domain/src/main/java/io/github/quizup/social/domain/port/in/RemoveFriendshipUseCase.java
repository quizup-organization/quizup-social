package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.FriendshipCommand;

import java.util.concurrent.CompletableFuture;

public interface RemoveFriendshipUseCase {
    CompletableFuture<String> remove(FriendshipCommand.RemoveFriendshipCommand command);

    default CompletableFuture<String> remove(String friendshipId) {
        return remove(new FriendshipCommand.RemoveFriendshipCommand(friendshipId));
    }

    default void removeAndWait(String friendshipId) {
        remove(friendshipId).join();
    }
}

