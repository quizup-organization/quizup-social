package io.github.quizup.social.infrastructure.out.persistence.mapper;

import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.infrastructure.out.persistence.entity.TopicFollowerEntity;

public final class TopicFollowerEntityMapper {
    private TopicFollowerEntityMapper() {
    }

    public static TopicFollower toDomain(TopicFollowerEntity entity) {
        return new TopicFollower(
                entity.getFollowId(),
                entity.getTopicId(),
                entity.getUserId(),
                entity.getFollowedAt(),
                entity.getUnfollowedAt()
        );
    }

    public static TopicFollowerEntity toEntity(TopicFollower topicFollower) {
        TopicFollowerEntity entity = new TopicFollowerEntity();
        entity.setFollowId(topicFollower.followId());
        entity.setTopicId(topicFollower.topicId());
        entity.setUserId(topicFollower.userId());
        entity.setFollowedAt(topicFollower.followedAt());
        entity.setUnfollowedAt(topicFollower.unfollowedAt());
        return entity;
    }
}

