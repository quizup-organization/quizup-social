package io.github.quizup.social.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.social.domain.command.UserFollowerCommand;
import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.exception.SocialExceptions;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Test Axon in-memory de l'agrégat {@link UserFollowerAggregate} via {@link AggregateTestFixture}.
 * <p>
 * 100 % in-memory : event store de l'agrégat en mémoire, aucun Postgres ni Axon Server.
 * L'unicité est portée par l'id déterministe ; l'agrégat ne fait aucune I/O (l'existence de la
 * cible n'est plus vérifiée : une cible inconnue n'a pas de projection).
 */
class UserFollowerAggregateTest {

    private final AggregateTestFixture<UserFollowerAggregate> fixture =
            new AggregateTestFixture<>(UserFollowerAggregate.class);

    @Test
    void followUser_appliesUserFollowedEvent() {
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
                .when(new UserFollowerCommand.UnfollowUserCommand("follower-1:followed-1", "follower-1"))
                .expectNoEvents();
    }

    @Test
    void unfollowUser_byOwner_appliesUserUnfollowedEvent() {
        fixture.given(new UserFollowerEvent.UserFollowedEvent(
                        "follower-1:followed-1", "follower-1", "followed-1", Instant.now()))
                .when(new UserFollowerCommand.UnfollowUserCommand("follower-1:followed-1", "follower-1"))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        UserFollowerEvent.UserUnfollowedEvent.class,
                        e -> ((UserFollowerEvent.UserUnfollowedEvent) e).followId().equals("follower-1:followed-1")));
    }

    @Test
    void unfollowUser_byAnotherUser_isRejected() {
        fixture.given(new UserFollowerEvent.UserFollowedEvent(
                        "follower-1:followed-1", "follower-1", "followed-1", Instant.now()))
                .when(new UserFollowerCommand.UnfollowUserCommand("follower-1:followed-1", "follower-2"))
                .expectException(SocialExceptions.NotFollowOwnerProblem.class);
    }
}
