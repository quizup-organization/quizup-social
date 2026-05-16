package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.infrastructure.out.persistence.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, String>, JpaSpecificationExecutor<FriendshipEntity> {

    /**
     * Récupère toutes les amitiés d'un utilisateur (bidirectionnel).
     * Retourne les FriendshipEntity où l'utilisateur est soit userId1 soit userId2.
     */
    @Query("SELECT f FROM FriendshipEntity f WHERE f.userId1 = :userId OR f.userId2 = :userId")
    List<FriendshipEntity> findByUserId(@Param("userId") String userId);

    /**
     * Vérifie si une amitié existe entre deux utilisateurs (dans n'importe quel ordre).
     */
    @Query("SELECT f FROM FriendshipEntity f WHERE " +
            "(f.userId1 = :user1 AND f.userId2 = :user2) OR " +
            "(f.userId1 = :user2 AND f.userId2 = :user1)")
    Optional<FriendshipEntity> findByUserIds(@Param("user1") String user1, @Param("user2") String user2);

    /**
     * Supprime une amitié entre deux utilisateurs (dans n'importe quel ordre).
     */
    @Modifying
    @Query("DELETE FROM FriendshipEntity f WHERE " +
            "(f.userId1 = :user1 AND f.userId2 = :user2) OR " +
            "(f.userId1 = :user2 AND f.userId2 = :user1)")
    void deleteFriendship(@Param("user1") String user1, @Param("user2") String user2);

    Optional<FriendshipEntity> findByUserId1AndUserId2(String userId1, String userId2);
}

