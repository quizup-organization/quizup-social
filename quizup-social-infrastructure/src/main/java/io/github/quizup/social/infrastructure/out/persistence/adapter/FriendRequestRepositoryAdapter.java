package io.github.quizup.social.infrastructure.out.persistence.adapter;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.common.infrastructure.adapter.AnnotationSearchableEntity;
import io.github.quizup.common.infrastructure.adapter.JpaSearchAdapter;
import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.model.FriendRequestStatus;
import io.github.quizup.social.domain.port.out.FriendRequestRepositoryPort;
import io.github.quizup.social.infrastructure.out.persistence.entity.FriendRequestEntity;
import io.github.quizup.social.infrastructure.out.persistence.mapper.FriendRequestEntityMapper;
import io.github.quizup.social.infrastructure.out.persistence.repository.FriendRequestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FriendRequestRepositoryAdapter implements FriendRequestRepositoryPort {

    private final FriendRequestJpaRepository friendRequestJpaRepository;
    private final JpaSearchAdapter<FriendRequestEntity> friendRequestJpaSearchAdapter;

    public FriendRequestRepositoryAdapter(FriendRequestJpaRepository friendRequestJpaRepository) {
        this.friendRequestJpaRepository = friendRequestJpaRepository;
        this.friendRequestJpaSearchAdapter = new JpaSearchAdapter<>(friendRequestJpaRepository, new AnnotationSearchableEntity(FriendRequestEntity.class));
    }

    @Override
    public void save(FriendRequest friendRequest) {
        friendRequestJpaRepository.save(FriendRequestEntityMapper.toEntity(friendRequest));
    }

    @Override
    public Optional<FriendRequest> findById(String requestId) {
        return friendRequestJpaRepository.findById(requestId).map(FriendRequestEntityMapper::toDomain);
    }

    @Override
    public void deleteById(String requestId) {
        friendRequestJpaRepository.deleteById(requestId);
    }

    @Override
    public List<FriendRequest> findByTargetIdAndStatus(String targetId, FriendRequestStatus status) {
        return friendRequestJpaRepository.findByTargetIdAndStatus(targetId, status)
                .stream()
                .map(FriendRequestEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<FriendRequest> findBySenderIdAndStatus(String senderId, FriendRequestStatus status) {
        return friendRequestJpaRepository.findBySenderIdAndStatus(senderId, status)
                .stream()
                .map(FriendRequestEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<FriendRequest> findBySenderIdAndTargetId(String senderId, String targetId) {
        return friendRequestJpaRepository.findBySenderIdAndTargetId(senderId, targetId)
                .map(FriendRequestEntityMapper::toDomain);
    }

    @Override
    public PageResult<FriendRequest> findAll(SearchCriteria criteria) {
        return friendRequestJpaSearchAdapter.findAll(criteria)
                .map(FriendRequestEntityMapper::toDomain);
    }
}

