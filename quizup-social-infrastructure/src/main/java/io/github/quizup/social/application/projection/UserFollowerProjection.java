package io.github.quizup.social.application.projection;

import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserFollowerProjection {

    private final UserFollowerRepositoryPort userFollowerRepositoryPort;

    public UserFollowerProjection(UserFollowerRepositoryPort userFollowerRepositoryPort) {
        this.userFollowerRepositoryPort = userFollowerRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(UserFollowerEvent.UserFollowedEvent event) {
        userFollowerRepositoryPort.save(
                new UserFollower(
                        event.followId(),
                        event.followerId(),
                        event.followedId(),
                        event.followedAt()
                )
        );
    }

    @EventHandler
    @Transactional
    public void on(UserFollowerEvent.UserUnfollowedEvent event) {
        userFollowerRepositoryPort.deleteById(event.followId());
    }
}
