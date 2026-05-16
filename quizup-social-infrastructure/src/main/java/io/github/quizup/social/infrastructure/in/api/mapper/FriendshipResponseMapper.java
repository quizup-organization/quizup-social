package io.github.quizup.social.infrastructure.in.api.mapper;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchResponseMapper;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.infrastructure.in.api.response.FriendshipResponse;

import java.util.List;

public final class FriendshipResponseMapper {
    private FriendshipResponseMapper() {
    }

    public static FriendshipResponse toResponse(Friendship friendship) {
        return new FriendshipResponse(
                friendship.friendshipId(),
                friendship.userId1(),
                friendship.userId2(),
                friendship.friendsSince()
        );
    }

    public static List<FriendshipResponse> toResponse(List<Friendship> friendships) {
        return friendships.stream().map(FriendshipResponseMapper::toResponse).toList();
    }

    public static PageResponse<FriendshipResponse> toResponse(PageResult<Friendship> friendships) {
        return SearchResponseMapper.toSearchResponse(friendships, FriendshipResponseMapper::toResponse);
    }
}

