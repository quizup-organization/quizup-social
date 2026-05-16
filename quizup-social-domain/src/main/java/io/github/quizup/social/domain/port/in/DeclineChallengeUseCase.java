package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.ChallengeCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — Cas d'utilisation : refuser un défi
 */
public interface DeclineChallengeUseCase {

    /**
     * Refuse un défi existant
     */
    CompletableFuture<String> decline(ChallengeCommand.DeclineChallengeCommand command);

    default CompletableFuture<String> decline(String challengeId) {
        return decline(
                new ChallengeCommand.DeclineChallengeCommand(
                        challengeId
                )
        );
    }
}

