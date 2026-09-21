package io.github.quizup.social.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.social.domain.command.TopicFollowerCommand;
import io.github.quizup.social.domain.event.TopicFollowerEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Test Axon in-memory de l'agrégat {@link TopicFollowerAggregate}.
 * <p>
 * 100 % in-memory, sans dépendance externe : l'unicité vient de l'id déterministe et le suivi
 * est non destructif (re-suivi idempotent).
 */
class TopicFollowerAggregateTest {

    private final AggregateTestFixture<TopicFollowerAggregate> fixture =
            new AggregateTestFixture<>(TopicFollowerAggregate.class);

    @Test
    void followTopic_appliesTopicFollowedEvent() {
        fixture.givenNoPriorActivity()
                .when(new TopicFollowerCommand.FollowTopicCommand("user-1:topic-1", "topic-1", "user-1"))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        TopicFollowerEvent.TopicFollowedEvent.class,
                        e -> ((TopicFollowerEvent.TopicFollowedEvent) e).followId().equals("user-1:topic-1")
                                && ((TopicFollowerEvent.TopicFollowedEvent) e).topicId().equals("topic-1")
                                && ((TopicFollowerEvent.TopicFollowedEvent) e).userId().equals("user-1")));
    }

    @Test
    void followTopic_whenAlreadyFollowed_isIdempotent() {
        fixture.given(new TopicFollowerEvent.TopicFollowedEvent(
                        "user-1:topic-1", "topic-1", "user-1", Instant.now()))
                .when(new TopicFollowerCommand.FollowTopicCommand("user-1:topic-1", "topic-1", "user-1"))
                .expectNoEvents();
    }

    @Test
    void unfollowTopic_thenFollowAgain_reappliesFollowedEvent() {
        fixture.given(
                        new TopicFollowerEvent.TopicFollowedEvent(
                                "user-1:topic-1", "topic-1", "user-1", Instant.now()),
                        new TopicFollowerEvent.TopicUnfollowedEvent(
                                "user-1:topic-1", "topic-1", "user-1", Instant.now()))
                .when(new TopicFollowerCommand.FollowTopicCommand("user-1:topic-1", "topic-1", "user-1"))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        TopicFollowerEvent.TopicFollowedEvent.class,
                        e -> ((TopicFollowerEvent.TopicFollowedEvent) e).followId().equals("user-1:topic-1")));
    }

    @Test
    void unfollowTopic_whenAlreadyUnfollowed_isIdempotent() {
        fixture.given(new TopicFollowerEvent.TopicFollowedEvent(
                        "user-1:topic-1", "topic-1", "user-1", Instant.now()),
                        new TopicFollowerEvent.TopicUnfollowedEvent(
                                "user-1:topic-1", "topic-1", "user-1", Instant.now()))
                .when(new TopicFollowerCommand.UnfollowTopicCommand("user-1:topic-1"))
                .expectNoEvents();
    }
}
