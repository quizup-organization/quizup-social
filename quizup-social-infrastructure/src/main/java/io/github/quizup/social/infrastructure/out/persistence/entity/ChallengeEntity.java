package io.github.quizup.social.infrastructure.out.persistence.entity;

import io.github.quizup.social.domain.model.ChallengeStatus;
import io.github.quizup.common.domain.model.search.FieldType;
import io.github.quizup.common.domain.model.search.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * ChallengeEntity - Entité JPA pour la projection read-only d'un défi.
 */
@Getter
@Setter
@Entity
@Table(name = "challenge_entry", indexes = {
        @Index(name = "idx_challenge_entry_challenger", columnList = "challenger_id"),
        @Index(name = "idx_challenge_entry_challenged", columnList = "challenged_id"),
        @Index(name = "idx_challenge_entry_status", columnList = "status"),
        @Index(name = "idx_challenge_entry_topic", columnList = "topic_id"),
})
public class ChallengeEntity {

    @Id
    @Searchable(type = FieldType.STRING)
    @Column(name = "challenge_id", nullable = false)
    private String challengeId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "challenger_id", nullable = false)
    private String challengerId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "challenged_id", nullable = false)
    private String challengedId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "topic_id", nullable = false)
    private String topicId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "game_id")
    private String gameId;

    @Searchable(type = FieldType.STRING)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ChallengeStatus status;

    @Searchable(type = FieldType.DATE)
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Searchable(type = FieldType.DATE)
    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Searchable(type = FieldType.DATE)
    @Column(name = "declined_at")
    private Instant declinedAt;

    @Searchable(type = FieldType.DATE)
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
