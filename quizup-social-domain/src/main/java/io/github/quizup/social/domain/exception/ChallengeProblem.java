package io.github.quizup.social.domain.exception;

import io.github.quizup.common.domain.exception.BaseProblem;
import io.github.quizup.common.domain.exception.ProblemCategory;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de base pour toutes les exceptions métier liées au domaine Challenge
 */
public abstract class ChallengeProblem extends BaseProblem {

    private final String challengeId;

    protected ChallengeProblem(
            String challengeId,
            String type,
            ProblemCategory category,
            String title,
            String detail,
            Map<String, Object> context) {
        super(
            type,
            category,
            title,
            detail,
            mergeContext(context, challengeId)
        );
        this.challengeId = challengeId;
    }

    protected ChallengeProblem(
            String challengeId,
            String type,
            String title,
            String detail,
            Map<String, Object> context) {
        this(challengeId, type, ProblemCategory.BUSINESS_INVALID_COMMAND, title, detail, context);
    }

    protected ChallengeProblem(
            String challengeId,
            String type,
            String title,
            String detail) {
        this(challengeId, type, ProblemCategory.BUSINESS_INVALID_COMMAND, title, detail, null);
    }

    protected ChallengeProblem(
            String challengeId,
            String type,
            String title) {
        this(challengeId, type, title, null, null);
    }

    private static Map<String, Object> mergeContext(Map<String, Object> context, String challengeId) {
        Map<String, Object> merged = new HashMap<>();
        if (context != null) {
            merged.putAll(context);
        }
        merged.put("challengeId", challengeId);
        return merged;
    }

    public String getChallengeId() {
        return challengeId;
    }
}

