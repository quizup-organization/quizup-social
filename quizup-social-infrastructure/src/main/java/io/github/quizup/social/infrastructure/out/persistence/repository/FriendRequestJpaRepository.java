package io.github.quizup.social.infrastructure.out.persistence.repository;

import io.github.quizup.social.infrastructure.out.persistence.entity.FriendRequestEntity;
import io.github.quizup.social.domain.model.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestJpaRepository extends JpaRepository<FriendRequestEntity, String>, JpaSpecificationExecutor<FriendRequestEntity> {

    List<FriendRequestEntity> findByTargetIdAndStatus(String targetId, FriendRequestStatus status);

    List<FriendRequestEntity> findBySenderIdAndStatus(String senderId, FriendRequestStatus status);

    Optional<FriendRequestEntity> findBySenderIdAndTargetId(String senderId, String targetId);
}

