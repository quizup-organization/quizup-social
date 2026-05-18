package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.common.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.common.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.common.infrastructure.in.api.response.IdResponse;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.infrastructure.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.*;
import io.github.quizup.social.infrastructure.in.api.mapper.ChallengeResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.CreateChallengeRequest;
import io.github.quizup.social.infrastructure.in.api.response.ChallengeResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.github.quizup.social.infrastructure.in.api.ChallengeController.ENDPOINT;

/**
 * ChallengeController - API REST pour la gestion des défis entre joueurs
 */
@RestController
@RequestMapping(ENDPOINT)
public class ChallengeController {

    public static final String ENDPOINT = "/api/challenges";
    private static final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    private final CreateChallengeUseCase createChallengeUseCase;
    private final AcceptChallengeUseCase acceptChallengeUseCase;
    private final DeclineChallengeUseCase declineChallengeUseCase;
    private final GetChallengeUseCase getChallengeUseCase;
    private final SearchChallengeUseCase searchChallengeUseCase;

    public ChallengeController(CreateChallengeUseCase createChallengeUseCase,
                               AcceptChallengeUseCase acceptChallengeUseCase,
                               DeclineChallengeUseCase declineChallengeUseCase,
                               GetChallengeUseCase getChallengeUseCase,
                               SearchChallengeUseCase searchChallengeUseCase) {
        this.createChallengeUseCase = createChallengeUseCase;
        this.acceptChallengeUseCase = acceptChallengeUseCase;
        this.declineChallengeUseCase = declineChallengeUseCase;
        this.getChallengeUseCase = getChallengeUseCase;
        this.searchChallengeUseCase = searchChallengeUseCase;
    }

    @PostMapping("/search")
    public CompletableFuture<ResponseEntity<PageResponse<ChallengeResponse>>> search(@RequestBody SearchRequest searchRequest) {
        SearchCriteria searchCriteria = SearchRequestMapper.toSearchCriteria(searchRequest);
        return searchChallengeUseCase.search(
                        searchCriteria.filters(),
                        searchCriteria.sorts(),
                        searchCriteria.page()
                )
                .thenApply(ChallengeResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Créer un nouveau défi
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<IdResponse>> createChallenge(@RequestBody @Valid CreateChallengeRequest request) {
        String challengeId = UUID.randomUUID().toString();
        String challengerId = SecurityHelper.getUserId();
        return createChallengeUseCase.create(challengeId, challengerId, request.challengedId(), request.topicId())
                .thenApply(aggregateId -> ResponseEntityBuilder.creation(ENDPOINT, aggregateId));
    }

    /**
     * Accepter un défi
     */
    @PostMapping("/{challengeId}/accept")
    public CompletableFuture<ResponseEntity<IdResponse>> acceptChallenge(
            @PathVariable String challengeId
    ) {
        return acceptChallengeUseCase.accept(challengeId)
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * Refuser un défi
     */
    @PostMapping("/{challengeId}/decline")
    public CompletableFuture<ResponseEntity<IdResponse>> declineChallenge(
            @PathVariable String challengeId) {
        return declineChallengeUseCase.decline(challengeId)
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * Récupérer un défi par son ID
     */
    @GetMapping("/{challengeId}")
    public CompletableFuture<ResponseEntity<ChallengeResponse>> getChallengeById(
            @PathVariable String challengeId) {
        return getChallengeUseCase.getById(challengeId)
                .thenApply(ChallengeResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);

    }
}



