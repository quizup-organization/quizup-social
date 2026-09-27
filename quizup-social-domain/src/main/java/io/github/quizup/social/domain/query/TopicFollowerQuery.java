package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;

public interface TopicFollowerQuery {

    record SearchTopicFollowerQuery(SearchRequest request) implements TopicFollowerQuery {
    }

    record GetTopicFollowerByIdQuery(String followId) implements TopicFollowerQuery {
    }

    /**
     * Liste bornée des sujets suivis par un joueur, plus récents d'abord.
     */
    record GetTopicFollowsQuery(String userId, int limit) implements TopicFollowerQuery {
    }

    /**
     * État de suivi d'un sujet par un joueur (sans exception si absent).
     */
    record ExistsTopicFollowerQuery(String topicId, String userId) implements TopicFollowerQuery {
    }
}
