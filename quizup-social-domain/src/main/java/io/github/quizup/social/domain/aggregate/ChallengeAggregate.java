package io.github.quizup.social.domain.aggregate;

import io.github.quizup.social.domain.command.*;
import io.github.quizup.social.domain.event.*;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.ChallengeDeadline;
import io.github.quizup.social.domain.model.ChallengeLanguages;
import io.github.quizup.social.domain.model.ChallengeStatus;
import io.github.quizup.social.domain.port.out.ChallengeRunGamePort;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import io.github.quizup.social.domain.port.out.TopicAvailabilityPort;
import io.github.quizup.microservice.core.domain.model.i18n.Language;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * ChallengeAggregate — Cycle de vie d'un défi.
 * <p>
 * Flux :
 * - CreateChallenge → PENDING (orchestration du délai déléguée à la saga)
 * - AcceptChallenge → ACCEPTED (création de la partie déléguée à la saga ; refusé si expiré)
 * - DeclineChallenge → DECLINED / CancelChallenge → CANCELED / ExpireChallenge → EXPIRED
 * - RegisterChallengeRun → runs asynchrones (gameId validé)
 * - RecordChallengeRunResult → scores des runs ; les deux connus ⇒ COMPLETED + vainqueur
 * - CompleteChallenge → défi synchrone terminé (résultat autoritaire de la partie)
 */
@Aggregate
public class ChallengeAggregate {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeAggregate.class);

    @AggregateIdentifier
    private String challengeId;
    private String challengerId;
    private String challengedId;
    private String topicId;
    private String challengerGameId;
    private String challengedGameId;
    private String replayGameId;
    private ChallengeStatus status;
    private Instant createdAt;
    private Instant acceptedAt;
    private Instant declinedAt;
    private Instant expiresAt;
    private Integer challengerScore;
    private Integer challengedScore;
    private String winnerId;
    private Instant completedAt;

    protected ChallengeAggregate() {
    }

    @CommandHandler
    public ChallengeAggregate(ChallengeCommand.CreateChallengeCommand command,
                              ProfileRepositoryPort profileRepositoryPort,
                              TopicAvailabilityPort topicAvailabilityPort) {
        logger.debug("Handling CreateChallengeCommand: challengeId={}, challengerId={}, challengedId={}",
                command.challengeId(), command.challengerId(), command.challengedId());

        if (command.challengerId().equals(command.challengedId())) {
            throw new ChallengeExceptions.CannotChallengeSelfProblem(command.challengedId());
        }

        requireTopicCoversLanguages(profileRepositoryPort, topicAvailabilityPort,
                command.challengerId(), command.challengedId(), command.topicId());

        Instant now = Instant.now();
        Instant expiration = now.plus(ChallengeDeadline.CHALLENGE_EXPIRED_TIMEOUT);

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeCreatedEvent(
                        command.challengeId(),
                        command.challengerId(),
                        command.challengedId(),
                        command.topicId(),
                        now,
                        expiration
                )
        );
    }

    /**
     * Accepter un défi → créer la partie et la démarrer (via la saga).
     * Garde défensive : le thème doit couvrir les langues des deux joueurs (re-vérifié ici,
     * même si la création l'a déjà validé).
     */
    @CommandHandler
    public void handle(ChallengeCommand.AcceptChallengeCommand command,
                       ProfileRepositoryPort profileRepositoryPort,
                       TopicAvailabilityPort topicAvailabilityPort) {
        logger.debug("Handling AcceptChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }
        if (expiresAt != null && !Instant.now().isBefore(expiresAt)) {
            throw new ChallengeExceptions.ChallengeExpiredProblem(challengeId);
        }
        if (!challengedId.equals(command.playerId())) {
            throw new ChallengeExceptions.UnauthorizedChallengeActionProblem(challengeId, command.playerId());
        }

        requireTopicCoversLanguages(profileRepositoryPort, topicAvailabilityPort,
                challengerId, challengedId, topicId);

        String gameId = UUID.randomUUID().toString();

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeAcceptedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        gameId,
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(ChallengeCommand.DeclineChallengeCommand command) {
        logger.debug("Handling DeclineChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }
        if (!challengedId.equals(command.playerId())) {
            throw new ChallengeExceptions.UnauthorizedChallengeActionProblem(challengeId, command.playerId());
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeDeclinedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(ChallengeCommand.CancelChallengeCommand command) {
        logger.debug("Handling CancelChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }
        if (!challengerId.equals(command.playerId())) {
            throw new ChallengeExceptions.UnauthorizedChallengeActionProblem(challengeId, command.playerId());
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeCanceledEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        Instant.now()
                )
        );
    }

    @CommandHandler
    public void handle(ChallengeCommand.ExpireChallengeCommand command) {
        logger.debug("Handling ExpireChallengeCommand: challengeId={}", command.challengeId());

        if (!ChallengeStatus.PENDING.equals(status)) {
            return;
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeExpiredEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        Instant.now()
                )
        );
    }

    /**
     * Commande système : le défi accepté n'a pas pu aboutir à une partie (échec de création).
     * Idempotente : no-op si le défi n'est plus {@code ACCEPTED}.
     */
    @CommandHandler
    public void handle(ChallengeCommand.FailChallengeCommand command) {
        logger.debug("Handling FailChallengeCommand: challengeId={}, reason={}",
                command.challengeId(), command.reason());

        if (!ChallengeStatus.ACCEPTED.equals(status)) {
            return;
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeFailedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        command.reason(),
                        Instant.now()
                )
        );
    }

    /**
     * Enregistre le run asynchrone d'un participant. Autorisé tant que le défi n'est ni refusé
     * ni expiré ; un même joueur ne peut enregistrer qu'un seul run. Le {@code gameId} doit être
     * une partie asynchrone du même sujet appartenant au joueur (validation synchrone).
     */
    @CommandHandler
    public void handle(ChallengeCommand.RegisterChallengeRunCommand command,
                       ChallengeRunGamePort challengeRunGamePort) {
        logger.debug("Handling RegisterChallengeRunCommand: challengeId={}, playerId={}",
                command.challengeId(), command.playerId());

        if (!isOpenForCompletion()) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }
        if (!isParticipant(command.playerId())) {
            throw new ChallengeExceptions.UnauthorizedChallengeActionProblem(challengeId, command.playerId());
        }
        if (!challengeRunGamePort.isAsyncRunOwnedBy(command.gameId(), command.playerId(), topicId)) {
            throw new ChallengeExceptions.ChallengeRunGameInvalidProblem(challengeId, command.gameId());
        }

        boolean isChallenger = challengerId.equals(command.playerId());
        String currentRun = isChallenger ? challengerGameId : challengedGameId;
        if (currentRun != null && !currentRun.equals(command.gameId())) {
            throw new ChallengeExceptions.ChallengeRunAlreadyRegisteredProblem(challengeId, command.playerId());
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeRunRegisteredEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        command.playerId(),
                        command.gameId(),
                        Instant.now()
                )
        );
    }

    /**
     * Enregistre le score final d'un run asynchrone. Quand les deux scores sont connus, le défi
     * se termine avec le vainqueur (égalité si scores identiques).
     */
    @CommandHandler
    public void handle(ChallengeCommand.RecordChallengeRunResultCommand command) {
        logger.debug("Handling RecordChallengeRunResultCommand: challengeId={}, playerId={}",
                command.challengeId(), command.playerId());

        if (!isOpenForCompletion() || !isParticipant(command.playerId())) {
            return;
        }

        boolean isChallenger = challengerId.equals(command.playerId());
        if ((isChallenger ? challengerGameId : challengedGameId) == null) {
            return;
        }
        if ((isChallenger ? challengerScore : challengedScore) != null) {
            return;
        }

        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeRunResultRecordedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        command.playerId(),
                        command.score(),
                        Instant.now()
                )
        );

        if (challengerScore != null && challengedScore != null) {
            applyCompleted(winnerOf(challengerScore, challengedScore), challengerScore, challengedScore);
        }
    }

    /**
     * Complète un défi synchrone à partir du résultat autoritaire de la partie.
     */
    @CommandHandler
    public void handle(ChallengeCommand.CompleteChallengeCommand command) {
        logger.debug("Handling CompleteChallengeCommand: challengeId={}", command.challengeId());

        if (ChallengeStatus.COMPLETED.equals(status)) {
            return;
        }
        if (!isOpenForCompletion()) {
            throw new ChallengeExceptions.ChallengeNotPendingProblem(challengeId, status.name());
        }

        applyCompleted(command.winnerId(), command.challengerScore(), command.challengedScore());
    }

    private void applyCompleted(String winnerId, int challengerScore, int challengedScore) {
        AggregateLifecycle.apply(
                new ChallengeEvent.ChallengeCompletedEvent(
                        challengeId,
                        challengerId,
                        challengedId,
                        winnerId,
                        challengerScore,
                        challengedScore,
                        Instant.now()
                )
        );
    }

    private boolean isParticipant(String playerId) {
        return challengerId.equals(playerId) || challengedId.equals(playerId);
    }

    /**
     * Exige que le thème dispose d'assez de questions dans toutes les langues des deux joueurs
     * (sélection stricte côté game) — sinon le défi est refusé avant toute création.
     */
    private static void requireTopicCoversLanguages(ProfileRepositoryPort profileRepositoryPort,
                                                    TopicAvailabilityPort topicAvailabilityPort,
                                                    String challengerId,
                                                    String challengedId,
                                                    String topicId) {
        Set<Language> languages = ChallengeLanguages.of(
                profileRepositoryPort.getById(challengerId),
                profileRepositoryPort.getById(challengedId));
        if (!topicAvailabilityPort.coversAllLanguages(topicId, languages)) {
            throw new ChallengeExceptions.TopicNotAvailableInLanguageProblem(topicId, languages);
        }
    }

    private boolean isOpenForCompletion() {
        return ChallengeStatus.PENDING.equals(status) || ChallengeStatus.ACCEPTED.equals(status);
    }

    private String winnerOf(int challengerScore, int challengedScore) {
        int comparison = Integer.compare(challengerScore, challengedScore);
        if (comparison > 0) {
            return challengerId;
        }
        if (comparison < 0) {
            return challengedId;
        }
        return null;
    }

    // === Event Sourcing Handlers ===

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeCreatedEvent event) {
        this.challengeId = event.challengeId();
        this.challengerId = event.challengerId();
        this.challengedId = event.challengedId();
        this.topicId = event.topicId();
        this.status = ChallengeStatus.PENDING;
        this.createdAt = event.createdAt();
        this.expiresAt = event.expiresAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeAcceptedEvent event) {
        this.status = ChallengeStatus.ACCEPTED;
        this.acceptedAt = event.acceptedAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeDeclinedEvent event) {
        this.status = ChallengeStatus.DECLINED;
        this.declinedAt = event.declinedAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeCanceledEvent event) {
        this.status = ChallengeStatus.CANCELED;
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeFailedEvent event) {
        this.status = ChallengeStatus.CANCELED;
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeRunRegisteredEvent event) {
        if (event.playerId().equals(challengerId)) {
            this.challengerGameId = event.gameId();
        } else {
            this.challengedGameId = event.gameId();
        }
        // Le second run enregistré est le replay : c'est lui qui porte le résultat autoritaire.
        if (challengerGameId != null && challengedGameId != null) {
            this.replayGameId = event.gameId();
        }
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeRunResultRecordedEvent event) {
        if (event.playerId().equals(challengerId)) {
            this.challengerScore = event.score();
        } else {
            this.challengedScore = event.score();
        }
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeCompletedEvent event) {
        this.status = ChallengeStatus.COMPLETED;
        this.winnerId = event.winnerId();
        this.challengerScore = event.challengerScore();
        this.challengedScore = event.challengedScore();
        this.completedAt = event.completedAt();
    }

    @EventSourcingHandler
    public void on(ChallengeEvent.ChallengeExpiredEvent event) {
        this.status = ChallengeStatus.EXPIRED;
        this.expiresAt = event.expiredAt();
    }
}
