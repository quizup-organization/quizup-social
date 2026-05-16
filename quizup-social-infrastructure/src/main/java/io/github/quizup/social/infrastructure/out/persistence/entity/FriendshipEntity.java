package io.github.quizup.social.infrastructure.out.persistence.entity;

import io.github.quizup.common.domain.model.search.FieldType;
import io.github.quizup.common.domain.model.search.Searchable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Relation d'amitié unidirectionnelle simplifiée.
 * Une seule ligne par paire avec userId1 < userId2 (lexicographiquement).
 */
@Setter
@Getter
@Entity
@Table(name = "friendship",
        uniqueConstraints = @UniqueConstraint(name = "uq_friendship", columnNames = {"user_id_1", "user_id_2"}),
        indexes = {
                @Index(name = "idx_friendship_user_1", columnList = "user_id_1"),
                @Index(name = "idx_friendship_user_2", columnList = "user_id_2")
        })
public class FriendshipEntity {

    @Id
    @Searchable(type = FieldType.STRING)
    @Column(name = "friendship_id", nullable = false)
    private String friendshipId;

    @Searchable(type = FieldType.STRING)
    @Column(name = "user_id_1", nullable = false)
    private String userId1;

    @Searchable(type = FieldType.STRING)
    @Column(name = "user_id_2", nullable = false)
    private String userId2;

    @Searchable(type = FieldType.DATE)
    @Column(name = "friends_since", nullable = false)
    private Instant friendsSince;
}
