package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.ChallengeCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — Cas d'utilisation : accepter un défi
 */
public interface AcceptChallengeUseCase {

    /**
     * Accepte un défi existant
     */
    CompletableFuture<String> accept(ChallengeCommand.AcceptChallengeCommand command);

    default CompletableFuture<String> accept(String challengeId) {
        return accept(
                new ChallengeCommand.AcceptChallengeCommand(
                        challengeId
                )
        );
    }
}

