package io.github.quizup.social.application.service;

import io.github.quizup.microservice.core.domain.model.i18n.Language;
import io.github.quizup.social.domain.command.ChallengeCommand;
import io.github.quizup.social.domain.exception.ChallengeExceptions;
import io.github.quizup.social.domain.model.ChallengeProfile;
import io.github.quizup.social.domain.port.in.AcceptChallengeUseCase;
import io.github.quizup.social.domain.port.in.CancelChallengeUseCase;
import io.github.quizup.social.domain.port.in.CreateChallengeUseCase;
import io.github.quizup.social.domain.port.in.DeclineChallengeUseCase;
import io.github.quizup.social.domain.port.in.RegisterChallengeRunUseCase;
import io.github.quizup.social.domain.port.out.ChallengeRepositoryPort;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import io.github.quizup.social.domain.port.out.TopicAvailabilityPort;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service applicatif — délègue les commandes de défi au CommandGateway Axon.
 *
 * <p>L'acceptation est gardée en amont : le thème doit couvrir les langues des deux joueurs
 * (sélection stricte côté game), sinon le défi est refusé avec un Problem explicite.</p>
 */
@Service
public class ChallengeCommandService implements CreateChallengeUseCase, AcceptChallengeUseCase, DeclineChallengeUseCase, CancelChallengeUseCase, RegisterChallengeRunUseCase {

    private final CommandGateway commandGateway;
    private final ChallengeRepositoryPort challengeRepositoryPort;
    private final ProfileRepositoryPort profileRepositoryPort;
    private final TopicAvailabilityPort topicAvailabilityPort;

    public ChallengeCommandService(CommandGateway commandGateway,
                                   ChallengeRepositoryPort challengeRepositoryPort,
                                   ProfileRepositoryPort profileRepositoryPort,
                                   TopicAvailabilityPort topicAvailabilityPort) {
        this.commandGateway = commandGateway;
        this.challengeRepositoryPort = challengeRepositoryPort;
        this.profileRepositoryPort = profileRepositoryPort;
        this.topicAvailabilityPort = topicAvailabilityPort;
    }

    @Override
    public CompletableFuture<String> create(ChallengeCommand.CreateChallengeCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> accept(ChallengeCommand.AcceptChallengeCommand command) {
        challengeRepositoryPort.findById(command.challengeId()).ifPresent(challenge -> {
            Set<Language> languages = languagesOf(
                    profileRepositoryPort.getById(challenge.challengerId()),
                    profileRepositoryPort.getById(challenge.challengedId()));
            if (!topicAvailabilityPort.coversAllLanguages(challenge.topicId(), languages)) {
                throw new ChallengeExceptions.TopicNotAvailableInLanguageProblem(challenge.topicId(), languages);
            }
        });

        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> decline(ChallengeCommand.DeclineChallengeCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> cancel(ChallengeCommand.CancelChallengeCommand command) {
        return commandGateway.send(command);
    }

    @Override
    public CompletableFuture<String> registerRun(ChallengeCommand.RegisterChallengeRunCommand command) {
        return commandGateway.send(command);
    }

    /** Langues non nulles des joueurs fournis (union). */
    private static Set<Language> languagesOf(ChallengeProfile... profiles) {
        Set<Language> languages = new HashSet<>();
        for (ChallengeProfile profile : profiles) {
            if (profile != null && profile.language() != null) {
                languages.add(profile.language());
            }
        }
        return languages;
    }
}
