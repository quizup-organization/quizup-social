package io.github.quizup.social.domain.exception;

import io.github.quizup.common.domain.exception.ProblemCategory;
import io.github.quizup.social.domain.model.FriendRequestStatus;

import java.util.Map;

public final class SocialExceptions {

    private SocialExceptions() {
    }

    public static class CannotSendRequestToSelfProblem extends SocialProblem {
        public CannotSendRequestToSelfProblem(String userId) {
            super("urn:quizup:social:cannotSendToSelf",
                    "Cannot send friend request to yourself",
                    "User " + userId + " cannot send a friend request to themselves",
                    Map.of("userId", userId));
        }
    }

    public static class FriendRequestNotPendingProblem extends SocialProblem {
        public FriendRequestNotPendingProblem(String requestId, FriendRequestStatus currentStatus) {
            super("urn:quizup:social:requestNotPending",
                    "Friend request is not in PENDING status",
                    "Request " + requestId + " is currently " + currentStatus,
                    Map.of("requestId", requestId, "currentStatus", currentStatus));
        }
    }

    public static class NoFriendshipBetweenUsersProblem extends SocialProblem {
        public NoFriendshipBetweenUsersProblem(String user1, String user2) {
            super("urn:quizup:social:notFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "Friendship not found",
                    "No friendship found between user with id " + user1 + " and user with id " + user2,
                    Map.of("user1", user1, "user2", user2));
        }
    }

    public static class FriendRequestNotFoundException extends SocialProblem {
        public FriendRequestNotFoundException(String requestId) {
            super("urn:quizup:social:requestNotFound",
                    "Friend request not found",
                    "No friend request found with id " + requestId,
                    Map.of("requestId", requestId));
        }
    }

    public static class FriendShipAlreadyExistsProblem extends SocialProblem {
        public FriendShipAlreadyExistsProblem(String userId1, String userId2) {
            super("urn:quizup:friendship:alreadyExists",
                    "Friendship already exists",
                    "A friendship between user with id " + userId1 + " and user with id " + userId2 + " already exists",
                    Map.of("userId1", userId1, "userId2", userId2));
        }
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
}
