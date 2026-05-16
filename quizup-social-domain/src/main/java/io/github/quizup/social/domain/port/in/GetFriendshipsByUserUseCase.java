package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.domain.query.FriendshipQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetFriendshipsByUserUseCase {
    CompletableFuture<List<Friendship>> getByUser(FriendshipQuery.GetFriendshipsByUserQuery query);

    default CompletableFuture<List<Friendship>> getByUser(String userId) {
        return getByUser(
                new FriendshipQuery.GetFriendshipsByUserQuery(
                        userId
                )
        );
    }
}

