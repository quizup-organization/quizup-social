package io.github.quizup.social.infrastructure.out.persistence.adapter;

import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.adapter.AnnotationSearchableEntity;
import io.github.quizup.microservice.core.infrastructure.adapter.JpaSearchAdapter;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.port.out.UserFollowerRepositoryPort;
import io.github.quizup.social.infrastructure.out.persistence.entity.UserFollowerEntity;
import io.github.quizup.social.infrastructure.out.persistence.mapper.UserFollowerEntityMapper;
import io.github.quizup.social.infrastructure.out.persistence.repository.UserFollowerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserFollowerRepositoryAdapter implements UserFollowerRepositoryPort {

    private final UserFollowerJpaRepository userFollowerJpaRepository;
    private final JpaSearchAdapter<UserFollowerEntity> userFollowerSearchAdapter;

    public UserFollowerRepositoryAdapter(UserFollowerJpaRepository userFollowerJpaRepository) {
        this.userFollowerJpaRepository = userFollowerJpaRepository;
        this.userFollowerSearchAdapter = new JpaSearchAdapter<>(
                userFollowerJpaRepository,
                new AnnotationSearchableEntity(UserFollowerEntity.class)
        );
    }

    @Override
    public void save(UserFollower userFollower) {
        userFollowerJpaRepository.save(UserFollowerEntityMapper.toEntity(userFollower));
    }

    @Override
    public Optional<UserFollower> findById(String followId) {
        return userFollowerJpaRepository.findById(followId)
                .map(UserFollowerEntityMapper::toDomain);
    }

    @Override
    public void deleteById(String followId) {
        userFollowerJpaRepository.deleteById(followId);
    }

    @Override
    public boolean exists(String followerId, String followedId) {
        return userFollowerJpaRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    @Override
    public SearchResponse<UserFollower> findAll(SearchRequest request) {
        return userFollowerSearchAdapter.findAll(request)
                .map(UserFollowerEntityMapper::toDomain);
    }
}
