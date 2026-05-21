package io.github.quizup.social.application.service;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.axon.PageResponseTypes;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.port.in.SearchTopicFollowerUseCase;
import io.github.quizup.social.domain.query.TopicFollowerQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TopicFollowerQueryService implements SearchTopicFollowerUseCase {

    private final QueryGateway queryGateway;

    public TopicFollowerQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<PageResult<TopicFollower>> search(TopicFollowerQuery.SearchTopicFollowerQuery query) {
        return queryGateway.query(query, PageResponseTypes.pageResultOf(TopicFollower.class));
    }
}

