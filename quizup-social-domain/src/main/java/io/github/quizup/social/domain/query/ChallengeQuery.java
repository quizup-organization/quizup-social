package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.social.domain.model.ChallengeBox;
import io.github.quizup.social.domain.model.ChallengeStatus;

public interface ChallengeQuery {

    record SearchChallengeQuery(SearchRequest request) implements ChallengeQuery {
    }

    record GetChallengeByIdQuery(String challengeId) implements ChallengeQuery {
    }

    /**
     * Retrouve le défi lié à une partie : partie synchrone ({@code gameId}) ou run asynchrone
     * ({@code challengerGameId} / {@code challengedGameId}). Sert à router les résultats de partie.
     */
    record FindChallengeByGameIdQuery(String gameId) implements ChallengeQuery {
    }

    /**
     * Défis d'un joueur, filtrés par boîte et statut optionnel, plus récents d'abord.
     */
    record GetChallengeBoxQuery(
            String userId,
            ChallengeBox box,
            ChallengeStatus status,
            int page,
            int size
    ) implements ChallengeQuery {
    }

    /**
     * Nombre de défis reçus en attente (badge de navigation).
     */
    record CountPendingChallengesQuery(String userId) implements ChallengeQuery {
    }
}
