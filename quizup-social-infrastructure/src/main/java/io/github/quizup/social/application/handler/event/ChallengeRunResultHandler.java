package io.github.quizup.social.application.handler.event;

import io.github.quizup.game.domain.event.GameEvent;
import io.github.quizup.social.domain.command.ChallengeCommand;
import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Route les résultats de partie vers les défis concernés (event bus partagé) :
 * - partie synchrone d'un défi accepté → complétion directe avec le résultat autoritaire ;
 * - run asynchrone → enregistrement du score du joueur (le défi se complète quand les deux
 *   scores sont connus).
 */
@Component
@ProcessingGroup("challenge-run-result")
public class ChallengeRunResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeRunResultHandler.class);

    private final ChallengeRepositoryPort challengeRepositoryPort;
    private final CommandGateway commandGateway;

    public ChallengeRunResultHandler(ChallengeRepositoryPort challengeRepositoryPort,
                                     CommandGateway commandGateway) {
        this.challengeRepositoryPort = challengeRepositoryPort;
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(GameEvent.GameRunRecordedEvent event) {
        challengeRepositoryPort.findByGameId(event.gameId()).ifPresent(challenge -> {
            logger.debug("Run asynchrone terminé: challengeId={}, gameId={}, playerId={}, score={}",
                    challenge.challengeId(), event.gameId(), event.playerId(), event.score());
            commandGateway.send(new ChallengeCommand.RecordChallengeRunResultCommand(
                    challenge.challengeId(), event.playerId(), event.score()));
        });
    }

    @EventHandler
    public void on(GameEvent.GameEndedEvent event) {
        challengeRepositoryPort.findByGameId(event.gameId()).ifPresent(challenge -> {
            if (event.gameId().equals(challenge.gameId())) {
                // Défi accepté en direct : la partie synchrone porte le résultat autoritaire.
                logger.debug("Défi synchrone terminé: challengeId={}, gameId={}, winnerId={}",
                        challenge.challengeId(), event.gameId(), event.winnerId());
                commandGateway.send(new ChallengeCommand.CompleteChallengeCommand(
                        challenge.challengeId(),
                        event.winnerId(),
                        event.player1FinalScore(),
                        event.player2FinalScore()));
            } else if (isParticipant(challenge, event.player1Id())) {
                // Replay fantôme : le score du joueur 1 (propriétaire du run) complète le défi.
                logger.debug("Replay de run terminé: challengeId={}, gameId={}, playerId={}, score={}",
                        challenge.challengeId(), event.gameId(), event.player1Id(), event.player1FinalScore());
                commandGateway.send(new ChallengeCommand.RecordChallengeRunResultCommand(
                        challenge.challengeId(), event.player1Id(), event.player1FinalScore()));
            }
        });
    }

    private static boolean isParticipant(Challenge challenge, String playerId) {
        return playerId != null
                && (playerId.equals(challenge.challengerId()) || playerId.equals(challenge.challengedId()));
    }
}
