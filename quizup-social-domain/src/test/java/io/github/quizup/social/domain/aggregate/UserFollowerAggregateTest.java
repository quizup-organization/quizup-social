package io.github.quizup.social.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.social.domain.command.UserFollowerCommand;
import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test Axon in-memory de l'agrégat {@link UserFollowerAggregate} via {@link AggregateTestFixture}.
 * <p>
 * 100 % in-memory : event store de l'agrégat en mémoire, aucun Postgres ni Axon Server.
 */
class UserFollowerAggregateTest {

    private final ProfileRepositoryPort profileRepositoryPort = Mockito.mock(ProfileRepositoryPort.class);
    private final UserFollowerRepositoryPort userFollowerRepositoryPort = Mockito.mock(UserFollowerRepositoryPort.class);

    private final AggregateTestFixture<UserFollowerAggregate> fixture =
            new AggregateTestFixture<>(UserFollowerAggregate.class);

    @BeforeEach
    void setUp() {
        fixture.registerInjectableResource(profileRepositoryPort);
        fixture.registerInjectableResource(userFollowerRepositoryPort);
    }

    @Test
    void followUser_appliesUserFollowedEvent() {
        Mockito.when(profileRepositoryPort.existsById("follower-1")).thenReturn(true);
        Mockito.when(profileRepositoryPort.existsById("followed-1")).thenReturn(true);
        Mockito.when(userFollowerRepositoryPort.exists("follower-1", "followed-1")).thenReturn(false);

        UserFollowerCommand.FollowUserCommand command = new UserFollowerCommand.FollowUserCommand(
                "uf-1", "follower-1", "followed-1");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        UserFollowerEvent.UserFollowedEvent.class,
                        e -> ((UserFollowerEvent.UserFollowedEvent) e).followId().equals("uf-1")
                                && ((UserFollowerEvent.UserFollowedEvent) e).followerId().equals("follower-1")
                                && ((UserFollowerEvent.UserFollowedEvent) e).followedId().equals("followed-1")));
    }

    @Test
    void followUser_onSelf_isRejected() {
        Mockito.when(profileRepositoryPort.existsById("user-1")).thenReturn(true);

        UserFollowerCommand.FollowUserCommand command = new UserFollowerCommand.FollowUserCommand(
                "uf-1", "user-1", "user-1");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectException(SocialExceptions.CannotFollowSelfProblem.class);
    }

    @Test
    void followUser_whenAlreadyFollowed_isRejected() {
        Mockito.when(profileRepositoryPort.existsById("follower-1")).thenReturn(true);
        Mockito.when(profileRepositoryPort.existsById("followed-1")).thenReturn(true);
        Mockito.when(userFollowerRepositoryPort.exists("follower-1", "followed-1")).thenReturn(true);

        UserFollowerCommand.FollowUserCommand command = new UserFollowerCommand.FollowUserCommand(
                "uf-2", "follower-1", "followed-1");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectException(SocialExceptions.UserAlreadyFollowedProblem.class);
    }
}
