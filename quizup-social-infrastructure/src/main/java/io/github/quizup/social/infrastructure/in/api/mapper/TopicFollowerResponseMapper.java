package io.github.quizup.social.infrastructure.in.api.mapper;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchResponseMapper;
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
                topicFollower.followedAt(),
                topicFollower.unfollowedAt()
        );
    }

    public static PageResponse<TopicFollowerResponse> toResponse(PageResult<TopicFollower> pageResult) {
        return SearchResponseMapper.toSearchResponse(pageResult, TopicFollowerResponseMapper::toResponse);
    }
}

