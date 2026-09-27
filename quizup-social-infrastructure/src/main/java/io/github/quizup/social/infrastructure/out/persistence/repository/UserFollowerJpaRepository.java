package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.infrastructure.out.persistence.entity.UserFollowerEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFollowerJpaRepository extends JpaRepository<UserFollowerEntity, String>, JpaSpecificationExecutor<UserFollowerEntity> {

    boolean existsByFollowerIdAndFollowedId(String followerId, String followedId);

    long countByFollowerId(String followerId);

    long countByFollowedId(String followedId);

    List<UserFollowerEntity> findByFollowerIdOrderByFollowedAtDesc(String followerId, Limit limit);

    List<UserFollowerEntity> findByFollowedIdOrderByFollowedAtDesc(String followedId, Limit limit);
}
