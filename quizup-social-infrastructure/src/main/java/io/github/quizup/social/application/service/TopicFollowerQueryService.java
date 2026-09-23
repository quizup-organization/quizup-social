package io.github.quizup.social.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.port.in.GetTopicFollowerUseCase;
import io.github.quizup.social.domain.port.in.SearchTopicFollowerUseCase;
import io.github.quizup.social.domain.query.TopicFollowerQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TopicFollowerQueryService implements SearchTopicFollowerUseCase, GetTopicFollowerUseCase {

    private final QueryGateway queryGateway;

    public TopicFollowerQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<PageResult<TopicFollower>> search(TopicFollowerQuery.SearchTopicFollowerQuery query) {
        return queryGateway.query(query, QueryResponseTypes.pageResultOf(TopicFollower.class));
    }

    @Override
    public CompletableFuture<TopicFollower> getById(TopicFollowerQuery.GetTopicFollowerByIdQuery query) {
        return queryGateway.query(query, QueryResponseTypes.instanceOf(TopicFollower.class));
    }
}
