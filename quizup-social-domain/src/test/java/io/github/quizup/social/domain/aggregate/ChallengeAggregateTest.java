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

    @Test
    void acceptChallenge_byChallenged_appliesAcceptedEvent() {
        fixture.given(created())
                .when(new ChallengeCommand.AcceptChallengeCommand(CHALLENGE_ID, CHALLENGED))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeAcceptedEvent.class,
                        e -> CHALLENGED.equals(((ChallengeEvent.ChallengeAcceptedEvent) e).challengedId())));
    }

    @Test
    void acceptChallenge_byNonChallenged_isRejected() {
        fixture.given(created())
                .when(new ChallengeCommand.AcceptChallengeCommand(CHALLENGE_ID, "intruder"))
                .expectException(ChallengeExceptions.UnauthorizedChallengeActionProblem.class);
    }

    @Test
    void declineChallenge_byChallenged_appliesDeclinedEvent() {
        fixture.given(created())
                .when(new ChallengeCommand.DeclineChallengeCommand(CHALLENGE_ID, CHALLENGED))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeDeclinedEvent.class,
                        e -> CHALLENGED.equals(((ChallengeEvent.ChallengeDeclinedEvent) e).challengedId())));
    }

    @Test
    void declineChallenge_byNonChallenged_isRejected() {
        fixture.given(created())
                .when(new ChallengeCommand.DeclineChallengeCommand(CHALLENGE_ID, "intruder"))
                .expectException(ChallengeExceptions.UnauthorizedChallengeActionProblem.class);
    }

    @Test
    void cancelChallenge_byChallenger_appliesCanceledEvent() {
        fixture.given(created())
                .when(new ChallengeCommand.CancelChallengeCommand(CHALLENGE_ID, CHALLENGER))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeCanceledEvent.class,
                        e -> CHALLENGER.equals(((ChallengeEvent.ChallengeCanceledEvent) e).challengerId())));
    }

    @Test
    void cancelChallenge_byNonChallenger_isRejected() {
        fixture.given(created())
                .when(new ChallengeCommand.CancelChallengeCommand(CHALLENGE_ID, CHALLENGED))
                .expectException(ChallengeExceptions.UnauthorizedChallengeActionProblem.class);
    }

    @Test
    void cancelChallenge_whenNotPending_isRejected() {
        fixture.given(created(), declined())
                .when(new ChallengeCommand.CancelChallengeCommand(CHALLENGE_ID, CHALLENGER))
                .expectException(ChallengeExceptions.ChallengeNotPendingProblem.class);
    }

    private ChallengeEvent.ChallengeCreatedEvent created() {
        Instant now = Instant.now();
        return new ChallengeEvent.ChallengeCreatedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, TOPIC, now, now.plusSeconds(86_400));
    }

    private ChallengeEvent.ChallengeDeclinedEvent declined() {
        return new ChallengeEvent.ChallengeDeclinedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, Instant.now());
    }
}
