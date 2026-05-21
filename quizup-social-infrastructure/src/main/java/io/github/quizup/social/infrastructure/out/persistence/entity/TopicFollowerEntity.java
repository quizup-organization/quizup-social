package io.github.quizup.social.infrastructure.out.persistence.entity;

import io.github.quizup.common.domain.model.search.FieldType;
import io.github.quizup.common.domain.model.search.Searchable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "topic_follower", indexes = {
        @Index(name = "idx_topic_follower_topic", columnList = "topic_id"),
        @Index(name = "idx_topic_follower_user", columnList = "user_id"),
        @Index(name = "idx_topic_follower_followed_at", columnList = "followed_at")
})
public class TopicFollowerEntity {

    @Id
    @Searchable(type = FieldType.STRING)
    @Column(name = "follow_id", nullable = false)
    private String followId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "topic_id", nullable = false)
    private String topicId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Searchable(type = FieldType.DATE)
    @Column(name = "followed_at", nullable = false)
    private Instant followedAt;

    @Searchable(type = FieldType.DATE)
    @Column(name = "unfollowed_at")
    private Instant unfollowedAt;
}

