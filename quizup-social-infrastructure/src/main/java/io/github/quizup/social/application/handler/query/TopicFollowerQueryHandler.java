package io.github.quizup.social.application.handler.query;

import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.social.domain.exception.SocialExceptions;
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
    public SearchResponse<TopicFollower> handle(TopicFollowerQuery.SearchTopicFollowerQuery query) {
        return topicFollowerRepositoryPort.findAll(query.request());
    }

    @QueryHandler
    public TopicFollower handle(TopicFollowerQuery.GetTopicFollowerByIdQuery query) {
        return topicFollowerRepositoryPort.findById(query.followId())
                .orElseThrow(() -> new SocialExceptions.TopicFollowerNotFoundProblem(query.followId()));
    }
}
