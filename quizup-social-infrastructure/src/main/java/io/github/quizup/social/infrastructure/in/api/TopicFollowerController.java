package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.microservice.core.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.IdResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.FollowTopicUseCase;
import io.github.quizup.social.domain.port.in.SearchTopicFollowerUseCase;
import io.github.quizup.social.domain.port.in.UnfollowTopicUseCase;
import io.github.quizup.social.infrastructure.in.api.mapper.TopicFollowerResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.FollowTopicRequest;
import io.github.quizup.social.infrastructure.in.api.response.TopicFollowerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Suivi **non destructif** d'un topic. Aucun endpoint de vérification dédié : le client
 * détermine ce que suit l'utilisateur via {@code POST /search} (filtres {@code topicId} /
 * {@code userId}).
 */
@RestController
@RequestMapping(TopicFollowerController.ENDPOINT)
public class TopicFollowerController {

    public static final String ENDPOINT = "/api/topic-follows";

    private final FollowTopicUseCase followTopicUseCase;
    private final UnfollowTopicUseCase unfollowTopicUseCase;
    private final SearchTopicFollowerUseCase searchTopicFollowerUseCase;

    public TopicFollowerController(
            FollowTopicUseCase followTopicUseCase,
            UnfollowTopicUseCase unfollowTopicUseCase,
            SearchTopicFollowerUseCase searchTopicFollowerUseCase
    ) {
        this.followTopicUseCase = followTopicUseCase;
        this.unfollowTopicUseCase = unfollowTopicUseCase;
        this.searchTopicFollowerUseCase = searchTopicFollowerUseCase;
    }

    @PostMapping("/search")
    public CompletableFuture<ResponseEntity<PageResponse<TopicFollowerResponse>>> search(@RequestBody SearchRequest searchRequest) {
        SearchCriteria searchCriteria = SearchRequestMapper.toSearchCriteria(searchRequest);
        return searchTopicFollowerUseCase.search(
                        searchCriteria.filters(),
                        searchCriteria.sorts(),
                        searchCriteria.page()
                )
                .thenApply(TopicFollowerResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<IdResponse>> follow(@RequestBody FollowTopicRequest request) {
        String userId = SecurityHelper.getUserId();
        String followId = UUID.randomUUID().toString();
        return followTopicUseCase.follow(followId, request.topicId(), userId)
                .thenApply(_ -> ResponseEntityBuilder.creation(ENDPOINT, followId));
    }

    @DeleteMapping("/{followId}")
    public CompletableFuture<ResponseEntity<IdResponse>> unfollow(@PathVariable String followId) {
        return unfollowTopicUseCase.unfollow(followId)
                .thenApply(_ -> ResponseEntityBuilder.ok(followId));
    }
}
