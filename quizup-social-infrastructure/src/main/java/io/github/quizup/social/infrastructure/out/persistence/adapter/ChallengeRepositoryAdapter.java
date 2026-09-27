package io.github.quizup.social.infrastructure.out.persistence.adapter;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.model.ChallengeBox;
import io.github.quizup.social.domain.model.ChallengePage;
import io.github.quizup.social.domain.model.ChallengeStatus;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import io.github.quizup.social.infrastructure.out.persistence.entity.ChallengeEntity;
import io.github.quizup.social.infrastructure.out.persistence.mapper.ChallengeEntityMapper;
import io.github.quizup.social.infrastructure.out.persistence.repository.ChallengeJpaRepository;
import io.github.quizup.microservice.core.infrastructure.in.api.response.SearchResponse;
import io.github.quizup.microservice.core.infrastructure.in.api.request.SearchRequest;
import io.github.quizup.microservice.core.infrastructure.adapter.AnnotationSearchableEntity;
import io.github.quizup.microservice.core.infrastructure.adapter.JpaSearchAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Transactional(readOnly = true)
    public Optional<Challenge> findByGameId(String gameId) {
        return challengeJpaRepository.findByAnyGameId(gameId)
                .map(ChallengeEntityMapper::toDomain);
    }

    @Override
    public SearchResponse<Challenge> findAll(SearchRequest request) {
        return challengeJpaSearchAdapter.findAll(request)
                .map(ChallengeEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengePage findBox(String userId, ChallengeBox box, ChallengeStatus status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ChallengeEntity> result = switch (box) {
            case RECEIVED -> challengeJpaRepository.findReceived(userId, status, pageable);
            case SENT -> challengeJpaRepository.findSent(userId, status, pageable);
            case ALL -> challengeJpaRepository.findAllForPlayer(userId, status, pageable);
        };
        return ChallengePage.builder()
                .challenges(result.getContent().stream().map(ChallengeEntityMapper::toDomain).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long countPending(String userId) {
        return challengeJpaRepository.countByChallengedIdAndStatus(userId, ChallengeStatus.PENDING);
    }
}
