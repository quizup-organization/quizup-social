package io.github.quizup.social.infrastructure.out.persistence.entity;

import io.github.quizup.microservice.core.domain.model.search.FieldType;
import io.github.quizup.microservice.core.domain.model.search.Searchable;
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
@Table(name = "user_follower", indexes = {
        @Index(name = "idx_user_follower_follower", columnList = "follower_id"),
        @Index(name = "idx_user_follower_followed", columnList = "followed_id"),
        @Index(name = "idx_user_follower_followed_at", columnList = "followed_at")
})
public class UserFollowerEntity {

    @Id
    @Searchable(type = FieldType.STRING)
    @Column(name = "follow_id", nullable = false)
    private String followId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "follower_id", nullable = false)
    private String followerId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "followed_id", nullable = false)
    private String followedId;

    @Searchable(type = FieldType.DATE)
    @Column(name = "followed_at", nullable = false)
    private Instant followedAt;
}
