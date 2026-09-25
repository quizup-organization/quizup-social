package io.github.quizup.social.application.handler.query;

import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.social.domain.exception.SocialExceptions;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import io.github.quizup.social.domain.query.UserFollowerQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

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
}
