package io.github.quizup.social.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.port.in.GetChallengeUseCase;
import io.github.quizup.social.domain.port.in.SearchChallengeUseCase;
import io.github.quizup.social.domain.query.ChallengeQuery;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

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
        return queryGateway.query(query, QueryResponseTypes.instanceOf(Challenge.class));
    }

    @Override
    public CompletableFuture<SearchResponse<Challenge>> search(ChallengeQuery.SearchChallengeQuery query) {
        return queryGateway.query(query, QueryResponseTypes.searchResponseOf(Challenge.class));
    }
}
