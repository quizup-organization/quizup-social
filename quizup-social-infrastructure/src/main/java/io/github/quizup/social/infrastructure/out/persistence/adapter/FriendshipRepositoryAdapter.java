package io.github.quizup.social.infrastructure.out.persistence.adapter;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.common.infrastructure.adapter.AnnotationSearchableEntity;
import io.github.quizup.common.infrastructure.adapter.JpaSearchAdapter;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.domain.port.out.FriendshipRepositoryPort;
import io.github.quizup.social.infrastructure.out.persistence.entity.FriendshipEntity;
import io.github.quizup.social.infrastructure.out.persistence.mapper.FriendshipEntityMapper;
import io.github.quizup.social.infrastructure.out.persistence.repository.FriendshipJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FriendshipRepositoryAdapter implements FriendshipRepositoryPort {

    private final FriendshipJpaRepository friendshipJpaRepository;
    private final JpaSearchAdapter<FriendshipEntity> friendshipJpaSearchAdapter;

    public FriendshipRepositoryAdapter(FriendshipJpaRepository friendshipJpaRepository) {
        this.friendshipJpaRepository = friendshipJpaRepository;
        this.friendshipJpaSearchAdapter = new JpaSearchAdapter<>(friendshipJpaRepository, new AnnotationSearchableEntity(FriendshipEntity.class));
    }

    @Override
    public void save(Friendship friendship) {
        friendshipJpaRepository.save(FriendshipEntityMapper.toEntity(friendship));
    }

    @Override
    public Optional<Friendship> findById(String friendshipId) {
        return friendshipJpaRepository.findById(friendshipId).map(FriendshipEntityMapper::toDomain);
    }

    @Override
    public void deleteById(String friendshipId) {
        friendshipJpaRepository.deleteById(friendshipId);
    }

    @Override
    public List<Friendship> findByUserId(String userId) {
        return friendshipJpaRepository.findByUserId(userId)
                .stream()
                .map(FriendshipEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Friendship> findByUserIds(String userId1, String userId2) {
        return friendshipJpaRepository.findByUserIds(userId1, userId2).map(FriendshipEntityMapper::toDomain);
    }

    @Override
    public boolean existsBetweenUsers(String userId1, String userId2) {
        return friendshipJpaRepository.findByUserIds(userId1, userId2).isPresent();
    }

    @Override
    public PageResult<Friendship> findAll(SearchCriteria criteria) {
        return friendshipJpaSearchAdapter.findAll(criteria)
                .map(FriendshipEntityMapper::toDomain);
    }
}

