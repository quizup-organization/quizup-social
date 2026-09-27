package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.domain.model.ChallengeStatus;
import io.github.quizup.social.infrastructure.out.persistence.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour les projections de défis
 */
@Repository
public interface ChallengeJpaRepository extends JpaRepository<ChallengeEntity, String>, JpaSpecificationExecutor<ChallengeEntity> {

    @Query("""
            select c from ChallengeEntity c
            where c.challengedId = :userId
              and (:status is null or c.status = :status)
            order by c.createdAt desc
            """)
    Page<ChallengeEntity> findReceived(@Param("userId") String userId,
                                       @Param("status") ChallengeStatus status,
                                       Pageable pageable);

    @Query("""
            select c from ChallengeEntity c
            where c.challengerId = :userId
              and (:status is null or c.status = :status)
            order by c.createdAt desc
            """)
    Page<ChallengeEntity> findSent(@Param("userId") String userId,
                                   @Param("status") ChallengeStatus status,
                                   Pageable pageable);

    @Query("""
            select c from ChallengeEntity c
            where (c.challengedId = :userId or c.challengerId = :userId)
              and (:status is null or c.status = :status)
            order by c.createdAt desc
            """)
    Page<ChallengeEntity> findAllForPlayer(@Param("userId") String userId,
                                           @Param("status") ChallengeStatus status,
                                           Pageable pageable);

    long countByChallengedIdAndStatus(String challengedId, ChallengeStatus status);

    @Query("""
            select c from ChallengeEntity c
            where c.gameId = :gameId
               or c.challengerGameId = :gameId
               or c.challengedGameId = :gameId
            """)
    Optional<ChallengeEntity> findByAnyGameId(@Param("gameId") String gameId);
}
