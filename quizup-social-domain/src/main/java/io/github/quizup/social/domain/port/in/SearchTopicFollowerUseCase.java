package io.github.quizup.social.domain.port.in;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.query.TopicFollowerQuery;

import java.util.concurrent.CompletableFuture;

public interface SearchTopicFollowerUseCase {

    CompletableFuture<SearchResponse<TopicFollower>> search(TopicFollowerQuery.SearchTopicFollowerQuery query);

    default CompletableFuture<SearchResponse<TopicFollower>> search(SearchRequest request) {
        return search(new TopicFollowerQuery.SearchTopicFollowerQuery(request));
    }
}
