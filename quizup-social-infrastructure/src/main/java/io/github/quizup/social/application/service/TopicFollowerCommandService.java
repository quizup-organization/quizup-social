package io.github.quizup.social.application.service;

import io.github.quizup.social.domain.command.TopicFollowerCommand;
import io.github.quizup.social.domain.port.in.FollowTopicUseCase;
import io.github.quizup.social.domain.port.in.UnfollowTopicUseCase;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TopicFollowerCommandService implements FollowTopicUseCase, UnfollowTopicUseCase {

    private final CommandGateway commandGateway;

    public TopicFollowerCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<String> follow(TopicFollowerCommand.FollowTopicCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> unfollow(TopicFollowerCommand.UnfollowTopicCommand command) {
        return commandGateway.send(command);
    }
}

