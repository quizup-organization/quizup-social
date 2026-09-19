package io.github.quizup.social.domain.exception;

import io.github.quizup.microservice.core.domain.exception.ProblemCategory;

import java.util.Map;

public final class SocialExceptions {

    private SocialExceptions() {
    }

    public static class SenderUserNotFoundProblem extends SocialProblem {
        public SenderUserNotFoundProblem(String userId) {
            super("urn:quizup:social:senderNotFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "Sender user not found",
                    "User " + userId + " was not found",
                    Map.of("userId", userId));
        }
    }

    public static class TargetUserNotFoundProblem extends SocialProblem {
        public TargetUserNotFoundProblem(String userId) {
            super("urn:quizup:social:targetNotFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "Target user not found",
                    "User " + userId + " was not found",
                    Map.of("userId", userId));
        }
    }

    public static class TopicNotFoundProblem extends SocialProblem {
        public TopicNotFoundProblem(String topicId) {
            super("urn:quizup:social:topic:notFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "Topic not found",
                    "Topic " + topicId + " was not found",
                    Map.of("topicId", topicId));
        }
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

    public static class TopicAlreadyFollowedProblem extends SocialProblem {
        public TopicAlreadyFollowedProblem(String topicId, String userId) {
            super("urn:quizup:social:topic:alreadyFollowed",
                    "Topic already followed",
                    "User " + userId + " already follows topic " + topicId,
                    Map.of("topicId", topicId, "userId", userId));
        }
    }

    public static class TopicFollowIdMismatchProblem extends SocialProblem {
        public TopicFollowIdMismatchProblem(String followId) {
            super("urn:quizup:social:topic:followIdMismatch",
                    ProblemCategory.BUSINESS_INVALID_COMMAND,
                    "Topic follow id mismatch",
                    "The follow id " + followId + " does not match the existing follow",
                    Map.of("followId", followId));
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

    public static class UserAlreadyFollowedProblem extends SocialProblem {
        public UserAlreadyFollowedProblem(String followerId, String followedId) {
            super("urn:quizup:social:user:alreadyFollowed",
                    "User already followed",
                    "User " + followerId + " already follows user " + followedId,
                    Map.of("followerId", followerId, "followedId", followedId));
        }
    }
}
