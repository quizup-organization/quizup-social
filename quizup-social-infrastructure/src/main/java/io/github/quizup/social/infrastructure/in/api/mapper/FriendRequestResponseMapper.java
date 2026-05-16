package io.github.quizup.social.infrastructure.in.api.mapper;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchResponseMapper;
import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.infrastructure.in.api.response.FriendRequestResponse;
import io.github.quizup.social.infrastructure.in.api.response.FriendshipResponse;

import java.util.List;

public final class FriendRequestResponseMapper {
    private FriendRequestResponseMapper() {
    }

    public static FriendRequestResponse toResponse(FriendRequest friendRequest) {
        return new FriendRequestResponse(
                friendRequest.requestId(),
                friendRequest.senderId(),
                friendRequest.targetId(),
                friendRequest.sentAt()
        );
    }

    public static List<FriendRequestResponse> toResponse(List<FriendRequest> friendRequests) {
        return friendRequests.stream().map(FriendRequestResponseMapper::toResponse).toList();
    }

    public static PageResponse<FriendRequestResponse> toResponse(PageResult<FriendRequest> friendships) {
        return SearchResponseMapper.toSearchResponse(friendships, FriendRequestResponseMapper::toResponse);
    }
}

