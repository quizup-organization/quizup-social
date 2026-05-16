package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.infrastructure.out.persistence.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour les projections de défis
 */
@Repository
public interface ChallengeJpaRepository extends JpaRepository<ChallengeEntity, String>, JpaSpecificationExecutor<ChallengeEntity> {
}
