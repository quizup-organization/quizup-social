package io.github.quizup.social.application.handler.query;

import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.model.ChallengePage;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import io.github.quizup.social.domain.query.ChallengeQuery;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ChallengeQueryHandler — Gère les queries pour les défis via Axon QueryHandler
 * Délègue au port sortant ChallengeRepositoryPort
 */
@Component
public class ChallengeQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeQueryHandler.class);

    private final ChallengeRepositoryPort challengeRepositoryPort;

    public ChallengeQueryHandler(ChallengeRepositoryPort challengeRepositoryPort) {
        this.challengeRepositoryPort = challengeRepositoryPort;
    }

    @QueryHandler
    public Challenge handle(ChallengeQuery.GetChallengeByIdQuery query) {
        logger.debug("Handling GetChallengeByIdQuery: challengeId={}", query.challengeId());
        return challengeRepositoryPort.findById(query.challengeId())
                .orElseThrow(() -> new ChallengeExceptions.ChallengeNotFoundProblem(query.challengeId()));
    }

    @QueryHandler
    public Optional<Challenge> handle(ChallengeQuery.FindChallengeByGameIdQuery query) {
        logger.debug("Handling FindChallengeByGameIdQuery: gameId={}", query.gameId());
        return challengeRepositoryPort.findByGameId(query.gameId());
    }

    @QueryHandler
    public SearchResponse<Challenge> handle(ChallengeQuery.SearchChallengeQuery query) {
        logger.debug("Handling SearchChallengeQuery: query={}", query);
        return challengeRepositoryPort.findAll(query.request());
    }

    @QueryHandler
    public ChallengePage handle(ChallengeQuery.GetChallengeBoxQuery query) {
        logger.debug("Handling GetChallengeBoxQuery: userId={}, box={}, status={}",
                query.userId(), query.box(), query.status());
        return challengeRepositoryPort.findBox(
                query.userId(), query.box(), query.status(), query.page(), query.size());
    }

    @QueryHandler
    public long handle(ChallengeQuery.CountPendingChallengesQuery query) {
        logger.debug("Handling CountPendingChallengesQuery: userId={}", query.userId());
        return challengeRepositoryPort.countPending(query.userId());
    }
}
