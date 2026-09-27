package io.github.quizup.social.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.social.domain.command.ChallengeCommand;
import io.github.quizup.social.domain.event.ChallengeEvent;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.port.out.ChallengeRunGamePort;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    private final ChallengeRunGamePort challengeRunGamePort = mock(ChallengeRunGamePort.class);

    private final AggregateTestFixture<ChallengeAggregate> fixture =
            new AggregateTestFixture<>(ChallengeAggregate.class);

    @BeforeEach
    void setUp() {
        fixture.registerInjectableResource(challengeRunGamePort);
        when(challengeRunGamePort.isAsyncRunOwnedBy(anyString(), anyString(), eq(TOPIC))).thenReturn(true);
    }

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
    void registerRun_withInvalidGame_isRejected() {
        when(challengeRunGamePort.isAsyncRunOwnedBy(anyString(), anyString(), eq(TOPIC))).thenReturn(false);

        fixture.given(created())
                .when(new ChallengeCommand.RegisterChallengeRunCommand(CHALLENGE_ID, CHALLENGED, "game-1"))
                .expectException(ChallengeExceptions.ChallengeRunGameInvalidProblem.class);
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
    void acceptChallenge_whenExpired_isRejected() {
        Instant now = Instant.now();
        ChallengeEvent.ChallengeCreatedEvent expired =
                new ChallengeEvent.ChallengeCreatedEvent(
                        CHALLENGE_ID, CHALLENGER, CHALLENGED, TOPIC, now.minusSeconds(120), now.minusSeconds(60));

        fixture.given(expired)
                .when(new ChallengeCommand.AcceptChallengeCommand(CHALLENGE_ID, CHALLENGED))
                .expectException(ChallengeExceptions.ChallengeExpiredProblem.class);
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

    @Test
    void firstRunResult_appliesRunResultRecordedEvent() {
        fixture.given(created(), runRegistered(CHALLENGED, "game-1"))
                .when(new ChallengeCommand.RecordChallengeRunResultCommand(CHALLENGE_ID, CHALLENGED, 100))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeRunResultRecordedEvent.class,
                        e -> {
                            ChallengeEvent.ChallengeRunResultRecordedEvent recorded =
                                    (ChallengeEvent.ChallengeRunResultRecordedEvent) e;
                            return CHALLENGED.equals(recorded.playerId()) && recorded.score() == 100;
                        }));
    }

    @Test
    void secondRunResult_completesChallengeWithWinner() {
        fixture.given(created(),
                        runRegistered(CHALLENGER, "game-2"),
                        runRegistered(CHALLENGED, "game-1"),
                        runResult(CHALLENGED, 100))
                .when(new ChallengeCommand.RecordChallengeRunResultCommand(CHALLENGE_ID, CHALLENGER, 80))
                .expectEventsMatching(QuizUpAxonMatchers.hasPayloadMatching(
                        ChallengeEvent.ChallengeCompletedEvent.class,
                        e -> {
                            ChallengeEvent.ChallengeCompletedEvent completed =
                                    (ChallengeEvent.ChallengeCompletedEvent) e;
                            return CHALLENGED.equals(completed.winnerId())
                                    && completed.challengerScore() == 80
                                    && completed.challengedScore() == 100;
                        }));
    }

    @Test
    void secondRunResult_withEqualScores_completesAsDraw() {
        fixture.given(created(),
                        runRegistered(CHALLENGER, "game-2"),
                        runRegistered(CHALLENGED, "game-1"),
                        runResult(CHALLENGED, 120))
                .when(new ChallengeCommand.RecordChallengeRunResultCommand(CHALLENGE_ID, CHALLENGER, 120))
                .expectEventsMatching(QuizUpAxonMatchers.hasPayloadMatching(
                        ChallengeEvent.ChallengeCompletedEvent.class,
                        e -> ((ChallengeEvent.ChallengeCompletedEvent) e).winnerId() == null));
    }

    @Test
    void duplicateRunResult_isIgnored() {
        fixture.given(created(), runRegistered(CHALLENGED, "game-1"), runResult(CHALLENGED, 100))
                .when(new ChallengeCommand.RecordChallengeRunResultCommand(CHALLENGE_ID, CHALLENGED, 100))
                .expectNoEvents();
    }

    @Test
    void completeSyncChallenge_appliesCompletedEvent() {
        fixture.given(created(), accepted())
                .when(new ChallengeCommand.CompleteChallengeCommand(CHALLENGE_ID, CHALLENGER, 140, 90))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        ChallengeEvent.ChallengeCompletedEvent.class,
                        e -> {
                            ChallengeEvent.ChallengeCompletedEvent completed =
                                    (ChallengeEvent.ChallengeCompletedEvent) e;
                            return CHALLENGER.equals(completed.winnerId())
                                    && completed.challengerScore() == 140;
                        }));
    }

    @Test
    void completeAlreadyCompleted_isIgnored() {
        fixture.given(created(), accepted(), completed())
                .when(new ChallengeCommand.CompleteChallengeCommand(CHALLENGE_ID, CHALLENGER, 140, 90))
                .expectNoEvents();
    }

    private ChallengeEvent.ChallengeCreatedEvent created() {
        Instant now = Instant.now();
        return new ChallengeEvent.ChallengeCreatedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, TOPIC, now, now.plusSeconds(86_400));
    }

    private ChallengeEvent.ChallengeAcceptedEvent accepted() {
        return new ChallengeEvent.ChallengeAcceptedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, "game-0", Instant.now());
    }

    private ChallengeEvent.ChallengeDeclinedEvent declined() {
        return new ChallengeEvent.ChallengeDeclinedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, Instant.now());
    }

    private ChallengeEvent.ChallengeRunRegisteredEvent runRegistered(String playerId, String gameId) {
        return new ChallengeEvent.ChallengeRunRegisteredEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, playerId, gameId, Instant.now());
    }

    private ChallengeEvent.ChallengeRunResultRecordedEvent runResult(String playerId, int score) {
        return new ChallengeEvent.ChallengeRunResultRecordedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, playerId, score, Instant.now());
    }

    private ChallengeEvent.ChallengeCompletedEvent completed() {
        return new ChallengeEvent.ChallengeCompletedEvent(
                CHALLENGE_ID, CHALLENGER, CHALLENGED, CHALLENGER, 140, 90, Instant.now());
    }
}
