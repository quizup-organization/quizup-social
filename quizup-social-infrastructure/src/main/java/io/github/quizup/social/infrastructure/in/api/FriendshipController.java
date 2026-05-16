package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.common.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.common.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.common.infrastructure.in.api.response.IdResponse;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.social.domain.port.in.RemoveFriendshipUseCase;
import io.github.quizup.social.domain.port.in.SearchFriendshipUseCase;
import io.github.quizup.social.infrastructure.in.api.mapper.FriendshipResponseMapper;
import io.github.quizup.social.infrastructure.in.api.response.FriendshipResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static io.github.quizup.social.infrastructure.in.api.FriendshipController.ENDPOINT;


/**
 * FriendshipController — Gestion des amitiés établies.
 * Endpoints REST pour lister et supprimer des amitiés.
 */
@RestController
@RequestMapping(ENDPOINT)
public class FriendshipController {

    public static final String ENDPOINT = "/api/friendships";

    private final RemoveFriendshipUseCase removeFriendshipUseCase;
    private final SearchFriendshipUseCase searchFriendshipUseCase;

    public FriendshipController(
            RemoveFriendshipUseCase removeFriendshipUseCase,
            SearchFriendshipUseCase searchFriendshipUseCase
    ) {
        this.removeFriendshipUseCase = removeFriendshipUseCase;
        this.searchFriendshipUseCase = searchFriendshipUseCase;
    }

    @PostMapping("/search")
    public CompletableFuture<ResponseEntity<PageResponse<FriendshipResponse>>> search(@RequestBody SearchRequest searchRequest) {
        SearchCriteria searchCriteria = SearchRequestMapper.toSearchCriteria(searchRequest);
        return searchFriendshipUseCase.search(
                        searchCriteria.filters(),
                        searchCriteria.sorts(),
                        searchCriteria.page()
                )
                .thenApply(FriendshipResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * DELETE /friendships/{friendshipId}
     * Supprime une amitié.
     */
    @DeleteMapping("/{friendshipId}")
    public CompletableFuture<ResponseEntity<IdResponse>> removeFriendship(@PathVariable String friendshipId) {
        return removeFriendshipUseCase.remove(friendshipId)
                .thenApply(ResponseEntityBuilder::ok);
    }
}

