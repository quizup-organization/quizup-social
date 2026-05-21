package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.infrastructure.out.persistence.entity.TopicFollowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicFollowerJpaRepository extends JpaRepository<TopicFollowerEntity, String>, JpaSpecificationExecutor<TopicFollowerEntity> {

    boolean existsByTopicIdAndUserId(String topicId, String userId);
}

