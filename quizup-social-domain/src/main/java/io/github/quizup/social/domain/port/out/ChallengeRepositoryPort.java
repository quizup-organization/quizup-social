package io.github.quizup.social.domain.port.out;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.model.ChallengeBox;
import io.github.quizup.social.domain.model.ChallengePage;
import io.github.quizup.social.domain.model.ChallengeStatus;
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

    /**
     * Récupère le défi lié à une partie (synchrone ou run asynchrone).
     */
    Optional<Challenge> findByGameId(String gameId);

    SearchResponse<Challenge> findAll(SearchRequest request);

    /**
     * Défis d'un joueur par boîte et statut optionnel, plus récents d'abord.
     */
    ChallengePage findBox(String userId, ChallengeBox box, ChallengeStatus status, int page, int size);

    /**
     * Nombre de défis reçus en attente pour un joueur.
     */
    long countPending(String userId);
}

