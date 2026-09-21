package io.github.quizup.social.application.projection;

import io.github.quizup.social.domain.event.UserFollowerEvent;
import io.github.quizup.social.domain.model.FollowerIds;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Projection du suivi des joueurs. La clé est la <b>clé naturelle</b>
 * ({@code followerId:followedId}) : upsert/delete idempotent et tolérant aux doublons (rejeu).
 */
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
                        FollowerIds.user(event.followerId(), event.followedId()),
                        event.followerId(),
                        event.followedId(),
                        event.followedAt()
                )
        );
    }

    @EventHandler
    @Transactional
    public void on(UserFollowerEvent.UserUnfollowedEvent event) {
        userFollowerRepositoryPort.deleteById(
                FollowerIds.user(event.followerId(), event.followedId())
        );
    }
}
