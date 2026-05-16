package io.github.quizup.social.application.service;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.axon.PageResponseTypes;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.domain.port.in.GetFriendshipsByUserUseCase;
import io.github.quizup.social.domain.port.in.SearchFriendshipUseCase;
import io.github.quizup.social.domain.query.FriendshipQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FriendshipQueryService implements GetFriendshipsByUserUseCase, SearchFriendshipUseCase {

    private final QueryGateway queryGateway;

    public FriendshipQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<List<Friendship>> getByUser(FriendshipQuery.GetFriendshipsByUserQuery query) {
        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(Friendship.class));
    }

    @Override
    public CompletableFuture<PageResult<Friendship>> search(FriendshipQuery.SearchFriendshipQuery query) {
        return queryGateway.query(query, PageResponseTypes.pageResultOf(Friendship.class));
    }
}

