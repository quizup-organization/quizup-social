package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.query.ChallengeQuery;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.Challenge;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — Cas d'utilisation : récupérer un défi par ID
 */
public interface GetChallengeUseCase {

    /**
     * Récupère un défi par son identifiant
     */
    CompletableFuture<Challenge> getById(ChallengeQuery.GetChallengeByIdQuery query) throws ChallengeExceptions.ChallengeNotFoundProblem;

    default CompletableFuture<Challenge> getById(String challengeId) throws ChallengeExceptions.ChallengeNotFoundProblem {
        return getById(
                new ChallengeQuery.GetChallengeByIdQuery(
                        challengeId
                )
        );
    }
}

