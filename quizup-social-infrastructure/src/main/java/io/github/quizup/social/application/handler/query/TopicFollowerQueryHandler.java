package io.github.quizup.social.application.handler.query;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.port.out.TopicFollowerRepositoryPort;
import io.github.quizup.social.domain.query.TopicFollowerQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class TopicFollowerQueryHandler {

    private final TopicFollowerRepositoryPort topicFollowerRepositoryPort;

    public TopicFollowerQueryHandler(TopicFollowerRepositoryPort topicFollowerRepositoryPort) {
        this.topicFollowerRepositoryPort = topicFollowerRepositoryPort;
    }

    @QueryHandler
    public PageResult<TopicFollower> handle(TopicFollowerQuery.SearchTopicFollowerQuery query) {
        return topicFollowerRepositoryPort.findAll(query);
    }
}

