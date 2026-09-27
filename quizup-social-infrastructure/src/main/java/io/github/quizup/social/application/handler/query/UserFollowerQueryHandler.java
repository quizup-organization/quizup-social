package io.github.quizup.social.application.handler.query;

import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.model.FollowDirection;
import io.github.quizup.social.domain.model.UserFollowCounts;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import io.github.quizup.social.domain.query.UserFollowerQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFollowerQueryHandler {

    private final UserFollowerRepositoryPort userFollowerRepositoryPort;

    public UserFollowerQueryHandler(UserFollowerRepositoryPort userFollowerRepositoryPort) {
        this.userFollowerRepositoryPort = userFollowerRepositoryPort;
    }

    @QueryHandler
    public SearchResponse<UserFollower> handle(UserFollowerQuery.SearchUserFollowerQuery query) {
        return userFollowerRepositoryPort.findAll(query.request());
    }

    @QueryHandler
    public UserFollower handle(UserFollowerQuery.GetUserFollowerByIdQuery query) {
        return userFollowerRepositoryPort.findById(query.followId())
                .orElseThrow(() -> new SocialExceptions.UserFollowerNotFoundProblem(query.followId()));
    }

    @QueryHandler
    public UserFollowCounts handle(UserFollowerQuery.GetUserFollowCountsQuery query) {
        return new UserFollowCounts(
                userFollowerRepositoryPort.countFollowing(query.userId()),
                userFollowerRepositoryPort.countFollowers(query.userId()));
    }

    @QueryHandler
    public List<UserFollower> handle(UserFollowerQuery.GetUserFollowsQuery query) {
        return query.direction() == FollowDirection.FOLLOWING
                ? userFollowerRepositoryPort.findFollowing(query.userId(), query.limit())
                : userFollowerRepositoryPort.findFollowers(query.userId(), query.limit());
    }

    @QueryHandler
    public boolean handle(UserFollowerQuery.ExistsUserFollowerQuery query) {
        return userFollowerRepositoryPort.exists(query.followerId(), query.followedId());
    }
}
