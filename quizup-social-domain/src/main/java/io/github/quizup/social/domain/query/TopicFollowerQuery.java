package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;

public interface TopicFollowerQuery {

    record SearchTopicFollowerQuery(SearchRequest request) implements TopicFollowerQuery {
    }

    record GetTopicFollowerByIdQuery(String followId) implements TopicFollowerQuery {
    }
}
