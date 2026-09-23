package io.github.quizup.social.infrastructure.in.api;

import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.microservice.core.infrastructure.in.api.ResponseEntityBuilder;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.in.api.response.IdResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchRequestMapper;
import io.github.quizup.microservice.security.SecurityHelper;
import io.github.quizup.social.domain.port.in.FollowTopicUseCase;
import io.github.quizup.social.domain.port.in.GetTopicFollowerUseCase;
import io.github.quizup.social.domain.port.in.SearchTopicFollowerUseCase;
import io.github.quizup.social.domain.port.in.UnfollowTopicUseCase;
import io.github.quizup.social.domain.model.FollowerIds;
import io.github.quizup.social.infrastructure.in.api.mapper.TopicFollowerResponseMapper;
import io.github.quizup.social.infrastructure.in.api.request.FollowTopicRequest;
import io.github.quizup.social.infrastructure.in.api.response.TopicFollowerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Suivi **non destructif** d'un topic. Lecture d'un suivi par son id déterministe
 * ({@code userId + ":" + topicId}), recherche via {@code POST /search}.
 */
@RestController
@RequestMapping(TopicFollowerController.ENDPOINT)
public class TopicFollowerController {

    public static final String ENDPOINT = "/api/topic-follows";

    private final FollowTopicUseCase followTopicUseCase;
    private final UnfollowTopicUseCase unfollowTopicUseCase;
    private final SearchTopicFollowerUseCase searchTopicFollowerUseCase;
    private final GetTopicFollowerUseCase getTopicFollowerUseCase;

    public TopicFollowerController(
            FollowTopicUseCase followTopicUseCase,
            UnfollowTopicUseCase unfollowTopicUseCase,
            SearchTopicFollowerUseCase searchTopicFollowerUseCase,
            GetTopicFollowerUseCase getTopicFollowerUseCase
    ) {
        this.followTopicUseCase = followTopicUseCase;
        this.unfollowTopicUseCase = unfollowTopicUseCase;
        this.searchTopicFollowerUseCase = searchTopicFollowerUseCase;
        this.getTopicFollowerUseCase = getTopicFollowerUseCase;
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

    /**
     * Lire un suivi par son id déterministe ({@code userId:topicId}).
     */
    @GetMapping("/{followId}")
    public CompletableFuture<ResponseEntity<TopicFollowerResponse>> getById(@PathVariable String followId) {
        return getTopicFollowerUseCase.getById(followId)
                .thenApply(TopicFollowerResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<IdResponse>> follow(@RequestBody FollowTopicRequest request) {
        String userId = SecurityHelper.getUserId();
        String followId = FollowerIds.topic(request.topicId(), userId);
        return followTopicUseCase.follow(followId, request.topicId(), userId)
                .thenApply(_ -> ResponseEntityBuilder.creation(ENDPOINT, followId));
    }

    @DeleteMapping("/{followId}")
    public CompletableFuture<ResponseEntity<Void>> unfollow(@PathVariable String followId) {
        return unfollowTopicUseCase.unfollow(followId)
                .thenApply(_ -> ResponseEntityBuilder.noContent());
    }
}
