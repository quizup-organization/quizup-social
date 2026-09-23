package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.microservice.core.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.IdResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.FollowUserUseCase;
import io.github.quizup.social.domain.port.in.GetUserFollowerUseCase;
import io.github.quizup.social.domain.port.in.SearchUserFollowerUseCase;
import io.github.quizup.social.domain.port.in.UnfollowUserUseCase;
import io.github.quizup.social.domain.model.FollowerIds;
import io.github.quizup.social.infrastructure.in.api.mapper.UserFollowerResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.FollowUserRequest;
import io.github.quizup.social.infrastructure.in.api.response.UserFollowerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Suivi **unidirectionnel** d'un joueur. Lecture d'un suivi par son id déterministe
 * ({@code followerId + ":" + followedId}) ; abonnements/abonnés/compteurs via
 * {@code POST /search} (filtres {@code followerId} / {@code followedId}).
 */
@RestController
@RequestMapping(UserFollowerController.ENDPOINT)
public class UserFollowerController {

    public static final String ENDPOINT = "/api/user-follows";

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final SearchUserFollowerUseCase searchUserFollowerUseCase;
    private final GetUserFollowerUseCase getUserFollowerUseCase;

    public UserFollowerController(
            FollowUserUseCase followUserUseCase,
            UnfollowUserUseCase unfollowUserUseCase,
            SearchUserFollowerUseCase searchUserFollowerUseCase,
            GetUserFollowerUseCase getUserFollowerUseCase
    ) {
        this.followUserUseCase = followUserUseCase;
        this.unfollowUserUseCase = unfollowUserUseCase;
        this.searchUserFollowerUseCase = searchUserFollowerUseCase;
        this.getUserFollowerUseCase = getUserFollowerUseCase;
    }

    @PostMapping("/search")
    public CompletableFuture<ResponseEntity<PageResponse<UserFollowerResponse>>> search(@RequestBody SearchRequest searchRequest) {
        SearchCriteria searchCriteria = SearchRequestMapper.toSearchCriteria(searchRequest);
        return searchUserFollowerUseCase.search(
                        searchCriteria.filters(),
                        searchCriteria.sorts(),
                        searchCriteria.page()
                )
                .thenApply(UserFollowerResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Lire un suivi par son id déterministe ({@code followerId:followedId}).
     */
    @GetMapping("/{followId}")
    public CompletableFuture<ResponseEntity<UserFollowerResponse>> getById(@PathVariable String followId) {
        return getUserFollowerUseCase.getById(followId)
                .thenApply(UserFollowerResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<IdResponse>> follow(@RequestBody FollowUserRequest request) {
        String followerId = SecurityHelper.getUserId();
        String followId = FollowerIds.user(followerId, request.followedId());
        return followUserUseCase.follow(followId, followerId, request.followedId())
                .thenApply(_ -> ResponseEntityBuilder.creation(ENDPOINT, followId));
    }

    @DeleteMapping("/{followId}")
    public CompletableFuture<ResponseEntity<Void>> unfollow(@PathVariable String followId) {
        return unfollowUserUseCase.unfollow(followId)
                .thenApply(_ -> ResponseEntityBuilder.noContent());
    }
}
