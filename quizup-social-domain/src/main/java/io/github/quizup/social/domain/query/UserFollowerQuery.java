package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;

public interface UserFollowerQuery {

    record SearchUserFollowerQuery(SearchRequest request) implements UserFollowerQuery {
    }

    record GetUserFollowerByIdQuery(String followId) implements UserFollowerQuery {
    }
}
