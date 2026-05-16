package io.github.quizup.social.application.saga;

import io.github.quizup.social.domain.command.ChallengeCommand;
import io.github.quizup.social.domain.event.ChallengeEvent;
import io.github.quizup.social.domain.model.ChallengeDeadline;
import io.github.quizup.game.domain.command.GameCommand;
import io.github.quizup.game.domain.model.GameMode;
import io.github.quizup.game.domain.model.GamePlayerType;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class ChallengeSaga {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Getter
    @Setter
    private String challengeId;

    @Getter
    @Setter
    private String topicId;

    @Getter
    @Setter
    private String challengerId;

    @Getter
    @Setter
    private String challengedId;

    @Getter
    @Setter
    private String challengeExpiredDeadlineId;

    @StartSaga
    @SagaEventHandler(associationProperty = "challengeId")
    public void on(ChallengeEvent.ChallengeCreatedEvent event) {
        this.challengeId = event.challengeId();
        this.topicId = event.topicId();
        this.challengerId = event.challengerId();
        this.challengedId = event.challengedId();

        challengeExpiredDeadlineId = deadlineManager.schedule(
                ChallengeDeadline.CHALLENGE_EXPIRED_TIMEOUT,
                ChallengeDeadline.CHALLENGE_EXPIRED
        );

        logger.debug("Challenge saga started: challengeId={}, deadlineId={}", challengeId, challengeExpiredDeadlineId);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "challengeId")
    public void on(ChallengeEvent.ChallengeAcceptedEvent event) {
        cancelChallengeExpiredDeadline();

        commandGateway.send(
                new GameCommand.CreateGameCommand(
                        event.gameId(),
                        topicId,
                        challengerId,
                        challengedId,
                        GameMode.SYNC,
                        GamePlayerType.HUMAN
                )
        );

        logger.info("Game creation orchestrated: challengeId={}, gameId={}", challengeId, event.gameId());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "challengeId")
    public void on(ChallengeEvent.ChallengeDeclinedEvent event) {
        cancelChallengeExpiredDeadline();
        logger.debug("Challenge saga ended after decline: challengeId={}", event.challengeId());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "challengeId")
    public void on(ChallengeEvent.ChallengeExpiredEvent event) {
        cancelChallengeExpiredDeadline();
        logger.debug("Challenge saga ended after expiration: challengeId={}", event.challengeId());
    }

    @DeadlineHandler(deadlineName = ChallengeDeadline.CHALLENGE_EXPIRED)
    public void onChallengeExpired() {
        logger.debug("Challenge expired deadline reached: challengeId={}", challengeId);
        commandGateway.send(new ChallengeCommand.ExpireChallengeCommand(challengeId));
    }

    private void cancelChallengeExpiredDeadline() {
        if (challengeExpiredDeadlineId != null) {
            deadlineManager.cancelSchedule(ChallengeDeadline.CHALLENGE_EXPIRED, challengeExpiredDeadlineId);
            challengeExpiredDeadlineId = null;
        }
    }
}

