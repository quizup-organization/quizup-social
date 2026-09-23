package io.github.quizup.social.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface ChallengeCommand {

    String challengeId();

    /**
     * Command pour accepter un défi (par le joueur défié).
     */
    record AcceptChallengeCommand(
            @TargetAggregateIdentifier String challengeId,
            String playerId
    ) implements ChallengeCommand{
    }

    /**
     * Command pour créer un défi entre deux joueurs.
     */
    record CreateChallengeCommand(
            @TargetAggregateIdentifier String challengeId,
            String challengerId,
            String challengedId,
            String topicId
    ) implements ChallengeCommand {
    }

    /**
     * Command pour accepter un défi.
     */
    record ExpireChallengeCommand(
            @TargetAggregateIdentifier String challengeId
    ) implements ChallengeCommand {
    }

    /**
     * Command pour refuser un défi (par le joueur défié).
     */
    record DeclineChallengeCommand(
            @TargetAggregateIdentifier String challengeId,
            String playerId
    ) implements ChallengeCommand {
    }

    /**
     * Command pour annuler un défi (par le joueur qui l'a lancé, tant qu'il est en attente).
     */
    record CancelChallengeCommand(
            @TargetAggregateIdentifier String challengeId,
            String playerId
    ) implements ChallengeCommand {
    }

    /**
     * Enregistre le run asynchrone d'un participant (jeu en différé). Le premier run
     * enregistré sert de référence au replay de l'adversaire.
     */
    record RegisterChallengeRunCommand(
            @TargetAggregateIdentifier String challengeId,
            String playerId,
            String gameId
    ) implements ChallengeCommand {
    }
}
