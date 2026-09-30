package io.github.quizup.social.domain.exception;

import io.github.quizup.microservice.core.domain.exception.ProblemCategory;
import io.github.quizup.microservice.core.domain.model.i18n.Language;

import java.util.Map;
import java.util.Set;

/**
 * Exceptions spécifiques au domaine Challenge
 */
public final class ChallengeExceptions {

    private ChallengeExceptions() {
        // Classe utilitaire
    }

    public static class TopicNotAvailableInLanguageProblem extends ChallengeProblem {
        public TopicNotAvailableInLanguageProblem(String topicId, Set<Language> languages) {
            super(topicId, "urn:quizup:challenge:topicNotAvailableInLanguage",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Topic not available in the players' languages",
                    "Le thème " + topicId + " n'a pas assez de questions dans " + languages,
                    Map.of("topicId", topicId,
                            "languages", languages.stream().map(Language::code).toList()));
        }
    }

    public static class ChallengeNotFoundProblem extends ChallengeProblem {
        public ChallengeNotFoundProblem(String challengeId) {
            super(challengeId, "urn:quizup:challenge:notFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "Challenge not found",
                    "The challenge " + challengeId + " was not found", null);
        }
    }

    public static class ChallengeNotPendingProblem extends ChallengeProblem {
        public ChallengeNotPendingProblem(String challengeId, String currentStatus) {
            super(challengeId, "urn:quizup:challenge:notPending",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Challenge is not pending",
                    "The challenge " + challengeId + " is not in PENDING status (current: " + currentStatus + ")",
                    Map.of("currentStatus", currentStatus));
        }
    }

    public static class CannotChallengeSelfProblem extends ChallengeProblem {
        public CannotChallengeSelfProblem(String userId) {
            super(userId, "urn:quizup:challenge:selfChallenge",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Cannot challenge yourself",
                    "A user cannot send a challenge to themselves",
                    Map.of("userId", userId));
        }
    }

    public static class UnauthorizedChallengeActionProblem extends ChallengeProblem {
        public UnauthorizedChallengeActionProblem(String challengeId, String userId) {
            super(challengeId, "urn:quizup:challenge:unauthorized",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Unauthorized challenge action",
                    "User " + userId + " is not authorized to perform this action on challenge " + challengeId,
                    Map.of("userId", userId));
        }
    }

    public static class ChallengeRunAlreadyRegisteredProblem extends ChallengeProblem {
        public ChallengeRunAlreadyRegisteredProblem(String challengeId, String userId) {
            super(challengeId, "urn:quizup:challenge:runAlreadyRegistered",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Challenge run already registered",
                    "User " + userId + " has already registered a run for challenge " + challengeId,
                    Map.of("userId", userId));
        }
    }

    public static class ChallengeExpiredProblem extends ChallengeProblem {
        public ChallengeExpiredProblem(String challengeId) {
            super(challengeId, "urn:quizup:challenge:expired",
                    ProblemCategory.BUSINESS_AGGREGATE,
                    "Challenge expired",
                    "The challenge " + challengeId + " has expired and can no longer be accepted",
                    null);
        }
    }

    public static class ChallengeRunGameInvalidProblem extends ChallengeProblem {
        public ChallengeRunGameInvalidProblem(String challengeId, String gameId) {
            super(challengeId, "urn:quizup:challenge:runGameInvalid",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Invalid challenge run game",
                    "Game " + gameId + " is not an async run of this challenge's topic owned by the player",
                    Map.of("gameId", gameId));
        }
    }
}

