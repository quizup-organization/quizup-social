package io.github.quizup.social.domain.model;

import java.time.Duration;

public interface ChallengeDeadline {
    String CHALLENGE_EXPIRED = "challenge-expired";
    Duration CHALLENGE_EXPIRED_TIMEOUT = Duration.ofHours(24);
}