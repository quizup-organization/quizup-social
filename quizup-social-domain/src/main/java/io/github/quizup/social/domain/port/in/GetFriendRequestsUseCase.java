package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.model.FriendRequestDirection;
import io.github.quizup.social.domain.query.FriendRequestQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetFriendRequestsUseCase {
    CompletableFuture<List<FriendRequest>> get(FriendRequestQuery.GetFriendRequestsQuery query);

    default CompletableFuture<List<FriendRequest>> get(String userId, FriendRequestDirection direction) {
        return get(
                new FriendRequestQuery.GetFriendRequestsQuery(
                        userId,
                        direction
                )
        );
    }
}

