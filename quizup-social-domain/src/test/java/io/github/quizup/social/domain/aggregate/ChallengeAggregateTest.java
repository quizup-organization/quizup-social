package io.github.quizup.social.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.social.domain.command.ChallengeCommand;
import io.github.quizup.social.domain.event.ChallengeEvent;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Test Axon in-memory de l'agrégat {@link ChallengeAggregate} via {@link AggregateTestFixture}.
 * <p>
 * 100 % in-memory : event store de l'agrégat en mémoire, aucun Postgres ni Axon Server.
 */
class ChallengeAggregateTest {

    private static final String CHALLENGE_ID = "ch-1";
    private static final String CHALLENGER = "challenger-1";
    private static final String CHALLENGED = "challenged-1";
    private static final String TOPIC = "topic-1";

    private final AggregateTestFixture<ChallengeAggregate> fixture =
            new AggregateTestFixture<>(ChallengeAggregate.class);

    @Test
    void createChallenge_appliesChallengeCreatedEvent() {
        ChallengeCommand.CreateChallengeCommand command = new ChallengeCommand.CreateChallengeCommand(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, TOPIC);

        fixture.givenNoPriorActivity()
                .when(command)
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeCreatedEvent.class,
                        e -> ((ChallengeEvent.ChallengeCreatedEvent) e).challengeId().equals(CHALLENGE_ID)
                                && ((ChallengeEvent.ChallengeCreatedEvent) e).challengerId().equals(CHALLENGER)));
    }

    @Test
    void registerRun_appliesChallengeRunRegisteredEvent() {
        fixture.given(created())
                .when(new ChallengeCommand.RegisterChallengeRunCommand(CHALLENGE_ID, CHALLENGED, "game-1"))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeRunRegisteredEvent.class,
                        e -> {
                            ChallengeEvent.ChallengeRunRegisteredEvent registered =
                                    (ChallengeEvent.ChallengeRunRegisteredEvent) e;
                            return CHALLENGED.equals(registered.playerId())
                                    && "game-1".equals(registered.gameId());
                        }));
    }

    @Test
    void registerRun_byNonParticipant_isRejected() {
        fixture.given(created())
                .when(new ChallengeCommand.RegisterChallengeRunCommand(CHALLENGE_ID, "intruder", "game-1"))
                .expectException(ChallengeExceptions.UnauthorizedChallengeActionProblem.class);
    }

    private ChallengeEvent.ChallengeCreatedEvent created() {
        Instant now = Instant.now();
        return new ChallengeEvent.ChallengeCreatedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, TOPIC, now, now.plusSeconds(86_400));
    }
}
