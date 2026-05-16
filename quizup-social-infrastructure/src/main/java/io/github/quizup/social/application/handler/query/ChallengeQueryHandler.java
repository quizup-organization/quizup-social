package io.github.quizup.social.application.handler.query;

import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import io.github.quizup.social.domain.query.ChallengeQuery;
import io.github.quizup.common.domain.model.search.PageResult;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
    public PageResult<Challenge> handle(ChallengeQuery.SearchChallengeQuery query) {
        logger.debug("Handling SearchChallengeQuery: query={}", query);
        return challengeRepositoryPort.findAll(query);
    }
}

