package io.github.quizup.social.domain.port.out;

import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.social.domain.model.UserFollower;

import java.util.List;
import java.util.Optional;

public interface UserFollowerRepositoryPort {

    void save(UserFollower userFollower);

    Optional<UserFollower> findById(String followId);

    void deleteById(String followId);

    boolean exists(String followerId, String followedId);

    SearchResponse<UserFollower> findAll(SearchRequest request);

    /**
     * Nombre d'abonnements du joueur (lignes dont il est {@code followerId}).
     */
    long countFollowing(String userId);

    /**
     * Nombre d'abonnés du joueur (lignes dont il est {@code followedId}).
     */
    long countFollowers(String userId);

    /**
     * Abonnements du joueur, plus récents d'abord.
     */
    List<UserFollower> findFollowing(String followerId, int limit);

    /**
     * Abonnés du joueur, plus récents d'abord.
     */
    List<UserFollower> findFollowers(String followedId, int limit);
}
