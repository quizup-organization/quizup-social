package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.common.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.common.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.common.infrastructure.in.api.response.IdResponse;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.infrastructure.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.*;
import io.github.quizup.social.infrastructure.in.api.mapper.FriendRequestResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.SendFriendRequest;
import io.github.quizup.social.infrastructure.in.api.response.FriendRequestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.github.quizup.social.infrastructure.in.api.FriendRequestController.ENDPOINT;

/**
 * FriendRequestController — Gestion des demandes d'ami (friend requests).
 * Endpoints REST conformes aux conventions.
 */
@RestController
@RequestMapping(ENDPOINT)
public class FriendRequestController {

    public static final String ENDPOINT = "/api/friend-requests";

    private final SendFriendRequestUseCase sendFriendRequestUseCase;
    private final AcceptFriendRequestUseCase acceptFriendRequestUseCase;
    private final RejectFriendRequestUseCase rejectFriendRequestUseCase;
    private final CancelFriendRequestUseCase cancelFriendRequestUseCase;
    private final SearchFriendRequestUseCase searchFriendRequestUseCase;

    public FriendRequestController(
            SearchFriendRequestUseCase searchFriendRequestUseCase,
            SendFriendRequestUseCase sendFriendRequestUseCase,
            AcceptFriendRequestUseCase acceptFriendRequestUseCase,
            RejectFriendRequestUseCase rejectFriendRequestUseCase,
            CancelFriendRequestUseCase cancelFriendRequestUseCase
    ) {
        this.sendFriendRequestUseCase = sendFriendRequestUseCase;
        this.acceptFriendRequestUseCase = acceptFriendRequestUseCase;
        this.rejectFriendRequestUseCase = rejectFriendRequestUseCase;
        this.cancelFriendRequestUseCase = cancelFriendRequestUseCase;
        this.searchFriendRequestUseCase = searchFriendRequestUseCase;
    }

    @PostMapping("/search")
    public CompletableFuture<ResponseEntity<PageResponse<FriendRequestResponse>>> search(@RequestBody SearchRequest searchRequest) {
        SearchCriteria searchCriteria = SearchRequestMapper.toSearchCriteria(searchRequest);
        return searchFriendRequestUseCase.search(
                        searchCriteria.filters(),
                        searchCriteria.sorts(),
                        searchCriteria.page()
                )
                .thenApply(FriendRequestResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * POST /friend-requests
     * Envoie une demande d'ami à un autre utilisateur.
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<IdResponse>> sendFriendRequest(@RequestBody SendFriendRequest request) {
        String userId = SecurityHelper.getUserId();
        String requestId = UUID.randomUUID().toString();

        return sendFriendRequestUseCase.send(requestId, userId, request.targetId())
                .thenApply(ignored -> ResponseEntityBuilder.creation(ENDPOINT, requestId));
    }

    /**
     * POST /friend-requests/{requestId}/accept
     * Accepte une demande d'ami.
     */
    @PostMapping("/{requestId}/accept")
    public CompletableFuture<ResponseEntity<IdResponse>> acceptFriendRequest(@PathVariable String requestId) {
        return acceptFriendRequestUseCase.accept(requestId)
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * POST /friend-requests/{requestId}/reject
     * Rejette une demande d'ami.
     */
    @PostMapping("/{requestId}/reject")
    public CompletableFuture<ResponseEntity<IdResponse>> rejectFriendRequest(@PathVariable String requestId) {
        return rejectFriendRequestUseCase.reject(requestId)
                .thenApply(ResponseEntityBuilder::ok);
    }

    /**
     * POST /friend-requests/{requestId}/cancel
     * Annule une demande d'ami envoyée.
     */
    @PostMapping("/{requestId}/cancel")
    public CompletableFuture<ResponseEntity<IdResponse>> cancelFriendRequest(@PathVariable String requestId) {
        return cancelFriendRequestUseCase.cancel(requestId)
                .thenApply(ResponseEntityBuilder::ok);
    }
}
