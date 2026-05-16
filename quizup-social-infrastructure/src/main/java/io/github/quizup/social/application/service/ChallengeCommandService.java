package io.github.quizup.social.application.service;

import io.github.quizup.social.domain.command.ChallengeCommand;
import io.github.quizup.social.domain.port.in.AcceptChallengeUseCase;
import io.github.quizup.social.domain.port.in.CreateChallengeUseCase;
import io.github.quizup.social.domain.port.in.DeclineChallengeUseCase;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service applicatif — Implémentation de CreateChallengeUseCase
 * Délègue au CommandGateway Axon
 */
@Service
public class ChallengeCommandService implements CreateChallengeUseCase, AcceptChallengeUseCase, DeclineChallengeUseCase {

    private final CommandGateway commandGateway;

    public ChallengeCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<String> create(ChallengeCommand.CreateChallengeCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> accept(ChallengeCommand.AcceptChallengeCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> decline(ChallengeCommand.DeclineChallengeCommand command) {
        return commandGateway.send(command);
    }
}

