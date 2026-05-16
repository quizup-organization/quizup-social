package io.github.quizup.social.infrastructure.out.persistence.adapter;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import io.github.quizup.social.infrastructure.out.persistence.entity.ChallengeEntity;
import io.github.quizup.social.infrastructure.out.persistence.mapper.ChallengeEntityMapper;
import io.github.quizup.social.infrastructure.out.persistence.repository.ChallengeJpaRepository;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.common.infrastructure.adapter.AnnotationSearchableEntity;
import io.github.quizup.common.infrastructure.adapter.JpaSearchAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter — Implémentation du port ChallengeRepositoryPort via JPA
 */
@Component
public class ChallengeRepositoryAdapter implements ChallengeRepositoryPort {

    private final ChallengeJpaRepository challengeJpaRepository;
    private final JpaSearchAdapter<ChallengeEntity> challengeJpaSearchAdapter;

    public ChallengeRepositoryAdapter(ChallengeJpaRepository challengeJpaRepository) {
        this.challengeJpaRepository = challengeJpaRepository;
        this.challengeJpaSearchAdapter = new JpaSearchAdapter<>(challengeJpaRepository, new AnnotationSearchableEntity(ChallengeEntity.class));
    }

    @Override
    @Transactional
    public void save(Challenge challenge) {
        challengeJpaRepository.save(ChallengeEntityMapper.toEntity(challenge));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Challenge> findById(String challengeId) {
        return challengeJpaRepository.findById(challengeId)
                .map(ChallengeEntityMapper::toDomain);
    }

    @Override
    public PageResult<Challenge> findAll(SearchCriteria criteria) {
        return challengeJpaSearchAdapter.findAll(criteria)
                .map(ChallengeEntityMapper::toDomain);
    }

}

