package io.github.quizup.social.domain.exception;

import io.github.quizup.common.domain.exception.ProblemCategory;

import java.util.Map;

/**
 * Exceptions spécifiques au domaine Challenge
 */
public final class ChallengeExceptions {

    private ChallengeExceptions() {
        // Classe utilitaire
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
}

