package io.github.quizup.social.infrastructure.in.api.mapper;

import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchResponseMapper;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.infrastructure.in.api.response.TopicFollowerResponse;

public final class TopicFollowerResponseMapper {
    private TopicFollowerResponseMapper() {
    }

    public static TopicFollowerResponse toResponse(TopicFollower topicFollower) {
        return new TopicFollowerResponse(
                topicFollower.followId(),
                topicFollower.topicId(),
                topicFollower.userId(),
                topicFollower.followedAt()
        );
    }

    public static PageResponse<TopicFollowerResponse> toResponse(PageResult<TopicFollower> pageResult) {
        return SearchResponseMapper.toSearchResponse(pageResult, TopicFollowerResponseMapper::toResponse);
    }
}

