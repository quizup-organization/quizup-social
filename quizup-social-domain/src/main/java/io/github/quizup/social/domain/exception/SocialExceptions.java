package io.github.quizup.social.domain.exception;

import io.github.quizup.microservice.core.domain.exception.ProblemCategory;

import java.util.Map;

public final class SocialExceptions {

    private SocialExceptions() {
    }

    public static class UserNotFoundProblem extends SocialProblem {
        public UserNotFoundProblem(String userId) {
            super("urn:quizup:social:user:notFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "User not found",
                    "User " + userId + " was not found",
                    Map.of("userId", userId));
        }
    }

    public static class CannotFollowSelfProblem extends SocialProblem {
        public CannotFollowSelfProblem(String userId) {
            super("urn:quizup:social:user:followSelf",
                    "Cannot follow yourself",
                    "User " + userId + " cannot follow themselves",
                    Map.of("userId", userId));
        }
    }
}
