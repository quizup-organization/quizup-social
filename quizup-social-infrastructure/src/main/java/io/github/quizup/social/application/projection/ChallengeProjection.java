package io.github.quizup.social.application.projection;

import io.github.quizup.social.domain.event.*;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.model.ChallengeStatus;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChallengeProjection — Gère la mise à jour du read-model via les événements
 * Utilise le port sortant pour persister
 */
@Component
@ProcessingGroup("challenge-projection")
public class ChallengeProjection {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeProjection.class);

    private final ChallengeRepositoryPort challengeRepositoryPort;

    public ChallengeProjection(ChallengeRepositoryPort challengeRepositoryPort) {
        this.challengeRepositoryPort = challengeRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(ChallengeEvent.ChallengeCreatedEvent event) {
        logger.debug("Projecting ChallengeCreatedEvent: challengeId={}", event.challengeId());

        Challenge challenge = new Challenge(
                event.challengeId(),
                event.challengerId(),
                event.challengedId(),
                event.topicId(),
                null,
                null,
                null,
                null,
                ChallengeStatus.PENDING,
                event.createdAt(),
                null,
                null,
                event.expiresAt()
        );

        challengeRepositoryPort.save(challenge);
        logger.info("Challenge projection created: challengeId={}", event.challengeId());
    }

    @EventHandler
    @Transactional
    public void on(ChallengeEvent.ChallengeAcceptedEvent event) {
        logger.debug("Projecting ChallengeAcceptedEvent: challengeId={}", event.challengeId());

        challengeRepositoryPort.findById(event.challengeId()).ifPresent(challenge ->
                challengeRepositoryPort.save(challenge.toBuilder()
                        .status(ChallengeStatus.ACCEPTED)
                        .gameId(event.gameId())
                        .acceptedAt(event.acceptedAt())
                        .build())
        );
    }

    @EventHandler
    @Transactional
    public void on(ChallengeEvent.ChallengeDeclinedEvent event) {
        logger.debug("Projecting ChallengeDeclinedEvent: challengeId={}", event.challengeId());

        challengeRepositoryPort.findById(event.challengeId()).ifPresent(challenge ->
                challengeRepositoryPort.save(challenge.toBuilder()
                        .status(ChallengeStatus.DECLINED)
                        .declinedAt(event.declinedAt())
                        .build())
        );
    }

    @EventHandler
    @Transactional
    public void on(ChallengeEvent.ChallengeCanceledEvent event) {
        logger.debug("Projecting ChallengeCanceledEvent: challengeId={}", event.challengeId());

        challengeRepositoryPort.findById(event.challengeId()).ifPresent(challenge ->
                challengeRepositoryPort.save(challenge.toBuilder()
                        .status(ChallengeStatus.CANCELED)
                        .build())
        );
    }

    @EventHandler
    @Transactional
    public void on(ChallengeEvent.ChallengeExpiredEvent event) {
        logger.debug("Projecting ChallengeExpiredEvent: challengeId={}", event.challengeId());

        challengeRepositoryPort.findById(event.challengeId()).ifPresent(challenge ->
                challengeRepositoryPort.save(challenge.toBuilder()
                        .status(ChallengeStatus.EXPIRED)
                        .build())
        );
    }

    @EventHandler
    @Transactional
    public void on(ChallengeEvent.ChallengeRunRegisteredEvent event) {
        logger.debug("Projecting ChallengeRunRegisteredEvent: challengeId={}, playerId={}",
                event.challengeId(), event.playerId());

        challengeRepositoryPort.findById(event.challengeId()).ifPresent(challenge -> {
            boolean isChallenger = event.playerId().equals(event.challengerId());
            String challengerGameId = isChallenger ? event.gameId() : challenge.challengerGameId();
            String challengedGameId = !isChallenger ? event.gameId() : challenge.challengedGameId();
            // Le second run enregistré est le replay (résultat autoritaire).
            String replayGameId = challengerGameId != null && challengedGameId != null
                    ? event.gameId()
                    : challenge.replayGameId();
            challengeRepositoryPort.save(challenge.toBuilder()
                    .challengerGameId(challengerGameId)
                    .challengedGameId(challengedGameId)
                    .replayGameId(replayGameId)
                    .build());
        });
    }
}
