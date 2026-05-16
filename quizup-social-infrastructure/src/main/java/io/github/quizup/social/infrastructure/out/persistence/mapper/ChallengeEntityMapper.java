package io.github.quizup.social.infrastructure.out.persistence.mapper;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.infrastructure.out.persistence.entity.ChallengeEntity;

/**
 * Mapper pour convertir entre Challenge (domaine) et ChallengeEntity (JPA)
 */
public final class ChallengeEntityMapper {

    private ChallengeEntityMapper() {
    }

    public static Challenge toDomain(ChallengeEntity entity) {
        return new Challenge(
                entity.getChallengeId(),
                entity.getChallengerId(),
                entity.getChallengedId(),
                entity.getTopicId(),
                entity.getGameId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getAcceptedAt(),
                entity.getDeclinedAt(),
                entity.getExpiresAt()
        );
    }

    public static ChallengeEntity toEntity(Challenge challenge) {
        ChallengeEntity entity = new ChallengeEntity();
        entity.setChallengeId(challenge.challengeId());
        entity.setChallengerId(challenge.challengerId());
        entity.setChallengedId(challenge.challengedId());
        entity.setTopicId(challenge.topicId());
        entity.setGameId(challenge.gameId());
        entity.setStatus(challenge.status());
        entity.setCreatedAt(challenge.createdAt());
        entity.setAcceptedAt(challenge.acceptedAt());
        entity.setDeclinedAt(challenge.declinedAt());
        entity.setExpiresAt(challenge.expiresAt());
        return entity;
    }
}

