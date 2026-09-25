package io.github.quizup.social.domain.port.in;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.query.UserFollowerQuery;

import java.util.concurrent.CompletableFuture;

public interface SearchUserFollowerUseCase {

    CompletableFuture<SearchResponse<UserFollower>> search(UserFollowerQuery.SearchUserFollowerQuery query);

    default CompletableFuture<SearchResponse<UserFollower>> search(SearchRequest request) {
        return search(new UserFollowerQuery.SearchUserFollowerQuery(request));
    }
}
