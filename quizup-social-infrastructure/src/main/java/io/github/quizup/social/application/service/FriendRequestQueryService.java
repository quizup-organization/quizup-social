package io.github.quizup.social.application.service;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.axon.PageResponseTypes;
import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.port.in.GetFriendRequestsUseCase;
import io.github.quizup.social.domain.port.in.SearchFriendRequestUseCase;
import io.github.quizup.social.domain.query.FriendRequestQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FriendRequestQueryService implements GetFriendRequestsUseCase, SearchFriendRequestUseCase {

    private final QueryGateway queryGateway;

    public FriendRequestQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<List<FriendRequest>> get(FriendRequestQuery.GetFriendRequestsQuery query) {
        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(FriendRequest.class));
    }

    @Override
    public CompletableFuture<PageResult<FriendRequest>> search(FriendRequestQuery.SearchFriendRequestQuery query) {
        return queryGateway.query(query, PageResponseTypes.pageResultOf(FriendRequest.class));
    }
}

