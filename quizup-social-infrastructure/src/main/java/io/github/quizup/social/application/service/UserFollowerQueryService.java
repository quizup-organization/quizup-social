package io.github.quizup.social.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.port.in.GetUserFollowerUseCase;
import io.github.quizup.social.domain.port.in.SearchUserFollowerUseCase;
import io.github.quizup.social.domain.query.UserFollowerQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserFollowerQueryService implements SearchUserFollowerUseCase, GetUserFollowerUseCase {

    private final QueryGateway queryGateway;

    public UserFollowerQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<PageResult<UserFollower>> search(UserFollowerQuery.SearchUserFollowerQuery query) {
        return queryGateway.query(query, QueryResponseTypes.pageResultOf(UserFollower.class));
    }

    @Override
    public CompletableFuture<UserFollower> getById(UserFollowerQuery.GetUserFollowerByIdQuery query) {
        return queryGateway.query(query, QueryResponseTypes.instanceOf(UserFollower.class));
    }
}
