package io.github.quizup.social.domain.port.out;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SearchCriteria;

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

    PageResult<Challenge> findAll(SearchCriteria criteria);
}

