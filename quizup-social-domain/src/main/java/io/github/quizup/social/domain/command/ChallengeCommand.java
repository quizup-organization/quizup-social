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
     * enregistré sert de référence au replay de l'adversaire. Le {@code gameId} est validé
     * (partie asynchrone du même sujet, appartenant au joueur).
     */
    record RegisterChallengeRunCommand(
            @TargetAggregateIdentifier String challengeId,
            String playerId,
            String gameId
    ) implements ChallengeCommand {
    }

    /**
     * Enregistre le score final d'un run (résultat asynchrone). Quand les deux scores sont
     * connus, le défi est complété avec le vainqueur calculé.
     */
    record RecordChallengeRunResultCommand(
            @TargetAggregateIdentifier String challengeId,
            String playerId,
            int score
    ) implements ChallengeCommand {
    }

    /**
     * Complète un défi synchrone à partir du résultat autoritaire de la partie.
     */
    record CompleteChallengeCommand(
            @TargetAggregateIdentifier String challengeId,
            String winnerId,
            int challengerScore,
            int challengedScore
    ) implements ChallengeCommand {
    }
}
