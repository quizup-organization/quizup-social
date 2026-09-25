package io.github.quizup.social.domain.port.in;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.query.ChallengeQuery;

import java.util.concurrent.CompletableFuture;

public interface SearchChallengeUseCase {

    CompletableFuture<SearchResponse<Challenge>> search(ChallengeQuery.SearchChallengeQuery query);

    default CompletableFuture<SearchResponse<Challenge>> search(SearchRequest request) {
        return search(new ChallengeQuery.SearchChallengeQuery(request));
    }
}
