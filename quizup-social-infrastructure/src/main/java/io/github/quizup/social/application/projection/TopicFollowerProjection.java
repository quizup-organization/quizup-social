package io.github.quizup.social.application.projection;

import io.github.quizup.social.domain.event.TopicFollowerEvent;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.port.out.TopicFollowerRepositoryPort;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TopicFollowerProjection {

    private final TopicFollowerRepositoryPort topicFollowerRepositoryPort;

    public TopicFollowerProjection(TopicFollowerRepositoryPort topicFollowerRepositoryPort) {
        this.topicFollowerRepositoryPort = topicFollowerRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(TopicFollowerEvent.TopicFollowedEvent event) {
        topicFollowerRepositoryPort.save(
                new TopicFollower(
                        event.followId(),
                        event.topicId(),
                        event.userId(),
                        event.followedAt(),
                        null
                )
        );
    }

    @EventHandler
    @Transactional
    public void on(TopicFollowerEvent.TopicUnfollowedEvent event) {
        topicFollowerRepositoryPort.deleteById(event.followId());
    }
}

