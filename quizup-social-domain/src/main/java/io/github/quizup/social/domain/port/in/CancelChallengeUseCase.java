package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.ChallengeCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — Cas d'utilisation : annuler un défi
 */
public interface CancelChallengeUseCase {

    /**
     * Annule un défi existant (par son instigateur, tant qu'il est en attente)
     */
    CompletableFuture<String> cancel(ChallengeCommand.CancelChallengeCommand command);

    default CompletableFuture<String> cancel(String challengeId, String playerId) {
        return cancel(
                new ChallengeCommand.CancelChallengeCommand(
                        challengeId,
                        playerId
                )
        );
    }
}
