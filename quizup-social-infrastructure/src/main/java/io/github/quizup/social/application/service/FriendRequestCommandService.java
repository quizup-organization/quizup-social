package io.github.quizup.social.application.service;

import io.github.quizup.social.domain.command.FriendRequestCommand;
import io.github.quizup.social.domain.port.in.AcceptFriendRequestUseCase;
import io.github.quizup.social.domain.port.in.CancelFriendRequestUseCase;
import io.github.quizup.social.domain.port.in.RejectFriendRequestUseCase;
import io.github.quizup.social.domain.port.in.SendFriendRequestUseCase;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class FriendRequestCommandService implements SendFriendRequestUseCase, AcceptFriendRequestUseCase,
        RejectFriendRequestUseCase, CancelFriendRequestUseCase {

    private final CommandGateway commandGateway;

    public FriendRequestCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<String> send(FriendRequestCommand.SendFriendRequestCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> accept(FriendRequestCommand.AcceptFriendRequestCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> reject(FriendRequestCommand.RejectFriendRequestCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> cancel(FriendRequestCommand.CancelFriendRequestCommand command) {
        return commandGateway.send(command);
    }
}

