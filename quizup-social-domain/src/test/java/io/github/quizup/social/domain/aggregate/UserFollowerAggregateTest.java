package io.github.quizup.social.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.social.domain.command.UserFollowerCommand;
import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

/**
 * Test Axon in-memory de l'agrégat {@link UserFollowerAggregate} via {@link AggregateTestFixture}.
 * <p>
 * 100 % in-memory : event store de l'agrégat en mémoire, aucun Postgres ni Axon Server.
 * L'unicité est portée par l'id déterministe ; seule l'existence de la **cible** est vérifiée.
 */
class UserFollowerAggregateTest {

    private final ProfileRepositoryPort profileRepositoryPort = Mockito.mock(ProfileRepositoryPort.class);

    private final AggregateTestFixture<UserFollowerAggregate> fixture =
            new AggregateTestFixture<>(UserFollowerAggregate.class);

    @BeforeEach
    void setUp() {
        fixture.registerInjectableResource(profileRepositoryPort);
    }

    @Test
    void followUser_appliesUserFollowedEvent() {
        Mockito.when(profileRepositoryPort.existsById("followed-1")).thenReturn(true);

        UserFollowerCommand.FollowUserCommand command = new UserFollowerCommand.FollowUserCommand(
                "follower-1:followed-1", "follower-1", "followed-1");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        UserFollowerEvent.UserFollowedEvent.class,
                        e -> ((UserFollowerEvent.UserFollowedEvent) e).followId().equals("follower-1:followed-1")
                                && ((UserFollowerEvent.UserFollowedEvent) e).followerId().equals("follower-1")
                                && ((UserFollowerEvent.UserFollowedEvent) e).followedId().equals("followed-1")));
    }

    @Test
    void followUser_onSelf_isRejected() {
        UserFollowerCommand.FollowUserCommand command = new UserFollowerCommand.FollowUserCommand(
                "user-1:user-1", "user-1", "user-1");

        fixture.givenNoPriorActivity()
                .when(command)
                .expectException(SocialExceptions.CannotFollowSelfProblem.class);
    }

    @Test
    void followUser_whenAlreadyFollowed_isIdempotent() {
        fixture.given(new UserFollowerEvent.UserFollowedEvent(
                        "follower-1:followed-1", "follower-1", "followed-1", Instant.now()))
                .when(new UserFollowerCommand.FollowUserCommand(
                        "follower-1:followed-1", "follower-1", "followed-1"))
                .expectNoEvents();
    }

    @Test
    void unfollowUser_thenFollowAgain_reappliesFollowedEvent() {
        Mockito.when(profileRepositoryPort.existsById("followed-1")).thenReturn(true);

        fixture.given(
                        new UserFollowerEvent.UserFollowedEvent(
                                "follower-1:followed-1", "follower-1", "followed-1", Instant.now()),
                        new UserFollowerEvent.UserUnfollowedEvent(
                                "follower-1:followed-1", "follower-1", "followed-1", Instant.now()))
                .when(new UserFollowerCommand.FollowUserCommand(
                        "follower-1:followed-1", "follower-1", "followed-1"))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        UserFollowerEvent.UserFollowedEvent.class,
                        e -> ((UserFollowerEvent.UserFollowedEvent) e).followId().equals("follower-1:followed-1")));
    }

    @Test
    void unfollowUser_whenAlreadyUnfollowed_isIdempotent() {
        fixture.given(new UserFollowerEvent.UserFollowedEvent(
                        "follower-1:followed-1", "follower-1", "followed-1", Instant.now()),
                        new UserFollowerEvent.UserUnfollowedEvent(
                                "follower-1:followed-1", "follower-1", "followed-1", Instant.now()))
                .when(new UserFollowerCommand.UnfollowUserCommand("follower-1:followed-1"))
                .expectNoEvents();
    }
}
