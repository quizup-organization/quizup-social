package io.github.quizup.social.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public interface ChallengeCommand {

    String challengeId();

    /**
     * Command pour accepter un défi.
     */
    record AcceptChallengeCommand(
            @TargetAggregateIdentifier String challengeId
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
     * Command pour refuser un défi.
     */
    record DeclineChallengeCommand(
            @TargetAggregateIdentifier String challengeId
    ) implements ChallengeCommand {
    }
}
