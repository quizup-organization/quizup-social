package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.microservice.core.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.IdResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.*;
import io.github.quizup.social.infrastructure.in.api.mapper.ChallengeResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.CreateChallengeRequest;
import io.github.quizup.social.infrastructure.in.api.request.RegisterChallengeRunRequest;
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
    private final CancelChallengeUseCase cancelChallengeUseCase;
    private final GetChallengeUseCase getChallengeUseCase;
    private final SearchChallengeUseCase searchChallengeUseCase;
    private final RegisterChallengeRunUseCase registerChallengeRunUseCase;

    public ChallengeController(CreateChallengeUseCase createChallengeUseCase,
                               AcceptChallengeUseCase acceptChallengeUseCase,
                               DeclineChallengeUseCase declineChallengeUseCase,
                               CancelChallengeUseCase cancelChallengeUseCase,
                               GetChallengeUseCase getChallengeUseCase,
                               SearchChallengeUseCase searchChallengeUseCase,
                               RegisterChallengeRunUseCase registerChallengeRunUseCase) {
        this.createChallengeUseCase = createChallengeUseCase;
        this.acceptChallengeUseCase = acceptChallengeUseCase;
        this.declineChallengeUseCase = declineChallengeUseCase;
        this.cancelChallengeUseCase = cancelChallengeUseCase;
        this.getChallengeUseCase = getChallengeUseCase;
        this.searchChallengeUseCase = searchChallengeUseCase;
        this.registerChallengeRunUseCase = registerChallengeRunUseCase;
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
        return acceptChallengeUseCase.accept(challengeId, SecurityHelper.getUserId())
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * Refuser un défi
     */
    @PostMapping("/{challengeId}/decline")
    public CompletableFuture<ResponseEntity<IdResponse>> declineChallenge(
            @PathVariable String challengeId) {
        return declineChallengeUseCase.decline(challengeId, SecurityHelper.getUserId())
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * Annuler un défi (par son instigateur, tant qu'il est en attente) — transition d'état sur
     * l'agrégat, donc {@code POST /{id}/cancel} (jamais un {@code DELETE} : la ressource n'est pas
     * supprimée, son statut passe à {@code CANCELED}).
     */
    @PostMapping("/{challengeId}/cancel")
    public CompletableFuture<ResponseEntity<IdResponse>> cancelChallenge(
            @PathVariable String challengeId) {
        return cancelChallengeUseCase.cancel(challengeId, SecurityHelper.getUserId())
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * Enregistrer le run asynchrone du joueur connecté (jeu en différé) — action sur l'agrégat
     * défi (le run n'est pas une ressource adressable : ses ids sont exposés par
     * {@link ChallengeResponse}). Réponse {@code 200 IdResponse(gameId)}.
     */
    @PostMapping("/{challengeId}/runs")
    public CompletableFuture<ResponseEntity<IdResponse>> registerRun(
            @PathVariable String challengeId,
            @RequestBody @Valid RegisterChallengeRunRequest request) {
        String playerId = SecurityHelper.getUserId();
        return registerChallengeRunUseCase.registerRun(challengeId, playerId, request.gameId())
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



