package io.github.quizup.social.infrastructure.out.metrics.adapter;

import io.github.quizup.social.domain.port.out.SocialMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Adapter Micrometer du {@link SocialMetricsPort}.
 * <p>Tags communs {@code application}/{@code environment}/{@code version} ajoutés par le SDK.
 */
@Component
public class SocialMetricsAdapter implements SocialMetricsPort {

    private final MeterRegistry registry;

    public SocialMetricsAdapter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void topicFollowed() {
        increment("quizup.social.topic.follows");
    }

    @Override
    public void topicUnfollowed() {
        increment("quizup.social.topic.unfollows");
    }

    @Override
    public void userFollowed() {
        increment("quizup.social.user.follows");
    }

    @Override
    public void userUnfollowed() {
        increment("quizup.social.user.unfollows");
    }

    @Override
    public void challengeCreated(String topicId) {
        Counter.builder("quizup.social.challenges.created")
                .tag("topic", safe(topicId))
                .register(registry)
                .increment();
    }

    @Override
    public void challengeAccepted() {
        increment("quizup.social.challenges.accepted");
    }

    @Override
    public void challengeDeclined() {
        increment("quizup.social.challenges.declined");
    }

    @Override
    public void challengeExpired() {
        increment("quizup.social.challenges.expired");
    }

    @Override
    public void challengeRunRegistered() {
        increment("quizup.social.challenges.runs.registered");
    }

    private void increment(String name) {
        Counter.builder(name).register(registry).increment();
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
    }
}
