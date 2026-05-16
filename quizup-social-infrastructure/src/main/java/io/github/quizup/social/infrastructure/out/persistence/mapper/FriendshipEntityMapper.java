package io.github.quizup.social.infrastructure.out.persistence.mapper;

import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.infrastructure.out.persistence.entity.FriendshipEntity;

public final class FriendshipEntityMapper {
    private FriendshipEntityMapper() {
    }

    public static Friendship toDomain(FriendshipEntity entity) {
        return new Friendship(
                entity.getFriendshipId(),
                entity.getUserId1(),
                entity.getUserId2(),
                entity.getFriendsSince()
        );
    }

    public static FriendshipEntity toEntity(Friendship friendship) {
        FriendshipEntity entity = new FriendshipEntity();
        entity.setFriendshipId(friendship.friendshipId());
        entity.setUserId1(friendship.userId1());
        entity.setUserId2(friendship.userId2());
        entity.setFriendsSince(friendship.friendsSince());
        return entity;
    }
}

