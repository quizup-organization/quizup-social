package io.github.quizup.social.infrastructure.out.persistence.entity;

import io.github.quizup.common.domain.model.search.FieldType;
import io.github.quizup.common.domain.model.search.Searchable;
import io.github.quizup.social.domain.model.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "friend_request", indexes = {
        @Index(name = "idx_friend_request_sender", columnList = "sender_id"),
        @Index(name = "idx_friend_request_target", columnList = "target_id"),
        @Index(name = "idx_friend_request_status", columnList = "status")
})
public class FriendRequestEntity {

    @Id
    @Searchable(type = FieldType.STRING)
    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "target_id", nullable = false)
    private String targetId;

    @Searchable(type = FieldType.STRING)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendRequestStatus status;

    @Searchable(type = FieldType.DATE)
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Searchable(type = FieldType.DATE)
    @Column(name = "responded_at")
    private Instant respondedAt;
}
