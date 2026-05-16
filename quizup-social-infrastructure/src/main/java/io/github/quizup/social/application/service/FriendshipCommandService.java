package io.github.quizup.social.application.service;

import io.github.quizup.social.domain.command.FriendshipCommand;
import io.github.quizup.social.domain.port.in.RemoveFriendshipUseCase;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class FriendshipCommandService implements RemoveFriendshipUseCase {

    private final CommandGateway commandGateway;

    public FriendshipCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<String> remove(FriendshipCommand.RemoveFriendshipCommand command) {
        return commandGateway.send(command);
    }
}

