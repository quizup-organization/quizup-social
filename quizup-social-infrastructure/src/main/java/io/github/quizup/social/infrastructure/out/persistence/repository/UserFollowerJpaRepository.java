package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.infrastructure.out.persistence.entity.UserFollowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowerJpaRepository extends JpaRepository<UserFollowerEntity, String>, JpaSpecificationExecutor<UserFollowerEntity> {

    boolean existsByFollowerIdAndFollowedId(String followerId, String followedId);
}
