package io.github.quizup.social.application.service;

import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.port.in.GetChallengeUseCase;
import io.github.quizup.social.domain.port.in.SearchChallengeUseCase;
import io.github.quizup.social.domain.query.ChallengeQuery;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.axon.PageResponseTypes;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service applicatif — Implémentation des UseCases de lecture (queries)
 * Délègue au port sortant ChallengeRepositoryPort
 */
@Service
public class ChallengeQueryService implements GetChallengeUseCase, SearchChallengeUseCase {

    private final QueryGateway queryGateway;

    public ChallengeQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public CompletableFuture<Challenge> getById(ChallengeQuery.GetChallengeByIdQuery query) throws ChallengeExceptions.ChallengeNotFoundProblem {
        return queryGateway.query(query, ResponseTypes.instanceOf(Challenge.class));
    }

    @Override
    public CompletableFuture<PageResult<Challenge>> search(ChallengeQuery.SearchChallengeQuery query) {
        return queryGateway.query(query, PageResponseTypes.pageResultOf(Challenge.class));
    }
}

