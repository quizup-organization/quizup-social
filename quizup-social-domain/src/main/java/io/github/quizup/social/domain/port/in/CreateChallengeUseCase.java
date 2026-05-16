package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.command.ChallengeCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — Cas d'utilisation : créer un défi
 */
public interface CreateChallengeUseCase {

    /**
     * Crée un nouveau défi entre deux joueurs
     */
    CompletableFuture<String> create(ChallengeCommand.CreateChallengeCommand command);

    default CompletableFuture<String> create(
            String challengeId,
            String challengerId,
            String challengedId,
            String topicId) {
        return create(
                new ChallengeCommand.CreateChallengeCommand(
                        challengeId,
                        challengerId,
                        challengedId,
                        topicId
                )
        );
    }
}

