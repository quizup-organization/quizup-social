package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.ChallengeCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — Cas d'utilisation : enregistrer le run asynchrone d'un participant.
 */
public interface RegisterChallengeRunUseCase {

    CompletableFuture<String> registerRun(ChallengeCommand.RegisterChallengeRunCommand command);

    default CompletableFuture<String> registerRun(String challengeId, String playerId, String gameId) {
        return registerRun(
                new ChallengeCommand.RegisterChallengeRunCommand(challengeId, playerId, gameId)
        );
    }
}
