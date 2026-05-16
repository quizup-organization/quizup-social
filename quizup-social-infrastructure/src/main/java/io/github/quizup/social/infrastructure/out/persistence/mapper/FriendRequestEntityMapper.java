package io.github.quizup.social.infrastructure.out.persistence.mapper;

import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.infrastructure.out.persistence.entity.FriendRequestEntity;

public final class FriendRequestEntityMapper {
    private FriendRequestEntityMapper() {
    }

    public static FriendRequest toDomain(FriendRequestEntity entity) {
        return new FriendRequest(
                entity.getRequestId(),
                entity.getSenderId(),
                entity.getTargetId(),
                entity.getStatus(),
                entity.getSentAt(),
                entity.getRespondedAt()
        );
    }

    public static FriendRequestEntity toEntity(FriendRequest friendRequest) {
        FriendRequestEntity entity = new FriendRequestEntity();
        entity.setRequestId(friendRequest.requestId());
        entity.setSenderId(friendRequest.senderId());
        entity.setTargetId(friendRequest.targetId());
        entity.setStatus(friendRequest.status());
        entity.setSentAt(friendRequest.sentAt());
        entity.setRespondedAt(friendRequest.respondedAt());
        return entity;
    }
}

