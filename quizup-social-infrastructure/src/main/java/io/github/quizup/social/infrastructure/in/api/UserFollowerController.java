package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.microservice.core.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.IdResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.FollowUserUseCase;
import io.github.quizup.social.domain.port.in.SearchUserFollowerUseCase;
import io.github.quizup.social.domain.port.in.UnfollowUserUseCase;
import io.github.quizup.social.infrastructure.in.api.mapper.UserFollowerResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.FollowUserRequest;
import io.github.quizup.social.infrastructure.in.api.response.UserFollowerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Suivi **unidirectionnel** d'un joueur. Aucun endpoint de lecture dédié : les abonnements,
 * abonnés et compteurs s'obtiennent via {@code POST /search} (filtres {@code followerId} /
 * {@code followedId}), le calcul étant fait côté client.
 */
@RestController
@RequestMapping(UserFollowerController.ENDPOINT)
public class UserFollowerController {

    public static final String ENDPOINT = "/api/user-follows";

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final SearchUserFollowerUseCase searchUserFollowerUseCase;

    public UserFollowerController(
            FollowUserUseCase followUserUseCase,
            UnfollowUserUseCase unfollowUserUseCase,
            SearchUserFollowerUseCase searchUserFollowerUseCase
    ) {
        this.followUserUseCase = followUserUseCase;
        this.unfollowUserUseCase = unfollowUserUseCase;
        this.searchUserFollowerUseCase = searchUserFollowerUseCase;
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

    @PostMapping
    public CompletableFuture<ResponseEntity<IdResponse>> follow(@RequestBody FollowUserRequest request) {
        String followerId = SecurityHelper.getUserId();
        String followId = UUID.randomUUID().toString();
        return followUserUseCase.follow(followId, followerId, request.followedId())
                .thenApply(_ -> ResponseEntityBuilder.creation(ENDPOINT, followId));
    }

    @DeleteMapping("/{followId}")
    public CompletableFuture<ResponseEntity<IdResponse>> unfollow(@PathVariable String followId) {
        return unfollowUserUseCase.unfollow(followId)
                .thenApply(_ -> ResponseEntityBuilder.ok(followId));
    }
}
