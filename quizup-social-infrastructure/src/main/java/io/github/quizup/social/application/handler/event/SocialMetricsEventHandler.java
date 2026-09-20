package io.github.quizup.social.application.handler.event;

import io.github.quizup.social.domain.event.ChallengeEvent;
import io.github.quizup.social.domain.event.TopicFollowerEvent;
import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.port.out.SocialMetricsPort;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * Alimente les KPI métier du social (abonnements topics/joueurs, cycle de vie des défis).
 * <p>Handlers {@link DisallowReplay} : un replay de projection ne réincrémente pas les compteurs.
 */
@Component
public class SocialMetricsEventHandler {

    private final SocialMetricsPort metrics;

    public SocialMetricsEventHandler(SocialMetricsPort metrics) {
        this.metrics = metrics;
    }

    @EventHandler
    @DisallowReplay
    public void on(TopicFollowerEvent.TopicFollowedEvent event) {
        metrics.topicFollowed();
    }

    @EventHandler
    @DisallowReplay
    public void on(TopicFollowerEvent.TopicUnfollowedEvent event) {
        metrics.topicUnfollowed();
    }

    @EventHandler
    @DisallowReplay
    public void on(UserFollowerEvent.UserFollowedEvent event) {
        metrics.userFollowed();
    }

    @EventHandler
    @DisallowReplay
    public void on(UserFollowerEvent.UserUnfollowedEvent event) {
        metrics.userUnfollowed();
    }

    @EventHandler
    @DisallowReplay
    public void on(ChallengeEvent.ChallengeCreatedEvent event) {
        metrics.challengeCreated(event.topicId());
    }

    @EventHandler
    @DisallowReplay
    public void on(ChallengeEvent.ChallengeAcceptedEvent event) {
        metrics.challengeAccepted();
    }

    @EventHandler
    @DisallowReplay
    public void on(ChallengeEvent.ChallengeDeclinedEvent event) {
        metrics.challengeDeclined();
    }

    @EventHandler
    @DisallowReplay
    public void on(ChallengeEvent.ChallengeExpiredEvent event) {
        metrics.challengeExpired();
    }

    @EventHandler
    @DisallowReplay
    public void on(ChallengeEvent.ChallengeRunRegisteredEvent event) {
        metrics.challengeRunRegistered();
    }
}
