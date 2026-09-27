package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.social.domain.model.FollowDirection;

public interface UserFollowerQuery {

    record SearchUserFollowerQuery(SearchRequest request) implements UserFollowerQuery {
    }

    record GetUserFollowerByIdQuery(String followId) implements UserFollowerQuery {
    }

    /**
     * Compteurs d'abonnements d'un joueur (abonnements et abonnés).
     */
    record GetUserFollowCountsQuery(String userId) implements UserFollowerQuery {
    }

    /**
     * Liste bornée des abonnements d'un joueur, plus récents d'abord.
     */
    record GetUserFollowsQuery(String userId, FollowDirection direction, int limit) implements UserFollowerQuery {
    }

    /**
     * État de suivi entre deux joueurs (id déterministe, sans exception si absent).
     */
    record ExistsUserFollowerQuery(String followerId, String followedId) implements UserFollowerQuery {
    }
}
