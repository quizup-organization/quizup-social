package io.github.quizup.social.application.saga;

import io.github.quizup.social.domain.command.FriendshipCommand;
import io.github.quizup.social.domain.event.FriendRequestEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class FriendRequestSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @EndSaga
    @SagaEventHandler(associationProperty = "requestId")
    public void on(FriendRequestEvent.FriendRequestAcceptedEvent event) {
        commandGateway.send(
                new FriendshipCommand.CreateFriendshipCommand(
                        UUID.randomUUID().toString(),
                        event.senderId(),
                        event.targetId()
                )
        );
    }
}

