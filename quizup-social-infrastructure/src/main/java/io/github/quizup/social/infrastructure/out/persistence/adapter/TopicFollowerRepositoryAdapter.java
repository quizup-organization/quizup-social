package io.github.quizup.social.infrastructure.out.persistence.adapter;

import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.adapter.AnnotationSearchableEntity;
import io.github.quizup.microservice.core.infrastructure.adapter.JpaSearchAdapter;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.port.out.TopicFollowerRepositoryPort;
import io.github.quizup.social.infrastructure.out.persistence.entity.TopicFollowerEntity;
import io.github.quizup.social.infrastructure.out.persistence.mapper.TopicFollowerEntityMapper;
import io.github.quizup.social.infrastructure.out.persistence.repository.TopicFollowerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TopicFollowerRepositoryAdapter implements TopicFollowerRepositoryPort {

    private final TopicFollowerJpaRepository topicFollowerJpaRepository;
    private final JpaSearchAdapter<TopicFollowerEntity> topicFollowerSearchAdapter;

    public TopicFollowerRepositoryAdapter(TopicFollowerJpaRepository topicFollowerJpaRepository) {
        this.topicFollowerJpaRepository = topicFollowerJpaRepository;
        this.topicFollowerSearchAdapter = new JpaSearchAdapter<>(
                topicFollowerJpaRepository,
                new AnnotationSearchableEntity(TopicFollowerEntity.class)
        );
    }

    @Override
    public void save(TopicFollower topicFollower) {
        topicFollowerJpaRepository.save(TopicFollowerEntityMapper.toEntity(topicFollower));
    }

    @Override
    public Optional<TopicFollower> findById(String followId) {
        return topicFollowerJpaRepository.findById(followId)
                .map(TopicFollowerEntityMapper::toDomain);
    }

    @Override
    public void deleteById(String followId) {
        topicFollowerJpaRepository.deleteById(followId);
    }

    @Override
    public boolean exists(String topicId, String userId) {
        return topicFollowerJpaRepository.existsByTopicIdAndUserId(topicId, userId);
    }

    @Override
    public SearchResponse<TopicFollower> findAll(SearchRequest request) {
        return topicFollowerSearchAdapter.findAll(request)
                .map(TopicFollowerEntityMapper::toDomain);
    }
}
