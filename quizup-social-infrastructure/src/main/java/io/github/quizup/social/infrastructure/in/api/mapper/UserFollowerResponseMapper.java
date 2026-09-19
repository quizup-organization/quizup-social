package io.github.quizup.social.infrastructure.in.api.mapper;

import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.microservice.core.infrastructure.in.api.response.PageResponse;
import io.github.quizup.microservice.core.infrastructure.mapper.SearchResponseMapper;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.infrastructure.in.api.response.UserFollowerResponse;

public final class UserFollowerResponseMapper {
    private UserFollowerResponseMapper() {
    }

    public static UserFollowerResponse toResponse(UserFollower userFollower) {
        return new UserFollowerResponse(
                userFollower.followId(),
                userFollower.followerId(),
                userFollower.followedId(),
                userFollower.followedAt()
        );
    }

    public static PageResponse<UserFollowerResponse> toResponse(PageResult<UserFollower> pageResult) {
        return SearchResponseMapper.toSearchResponse(pageResult, UserFollowerResponseMapper::toResponse);
    }
}
