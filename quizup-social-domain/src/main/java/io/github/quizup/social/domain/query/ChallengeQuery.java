package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;

public interface ChallengeQuery {

    record SearchChallengeQuery(SearchRequest request) implements ChallengeQuery {
    }

    record GetChallengeByIdQuery(String challengeId) implements ChallengeQuery {
    }
}
