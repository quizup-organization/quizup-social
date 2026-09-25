package io.github.quizup.social.domain.port.out;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;

import java.util.Optional;

/**
 * Port sortant — Interface pour la persistence des défis
 */
public interface ChallengeRepositoryPort {

    /**
     * Persiste un défi
     */
    void save(Challenge challenge);

    /**
     * Récupère un défi par son ID
     */
    Optional<Challenge> findById(String challengeId);

    SearchResponse<Challenge> findAll(SearchRequest request);
}

