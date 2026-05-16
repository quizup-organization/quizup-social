package io.github.quizup.social.domain.exception;

import io.github.quizup.common.domain.exception.BaseProblem;
import io.github.quizup.common.domain.exception.ProblemCategory;

import java.util.Map;

public abstract class SocialProblem extends BaseProblem {


    protected SocialProblem(
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
                context
        );
    }
    protected SocialProblem(
            String type,
            String title,
            String detail,
            Map<String, Object> context) {
        super(type, ProblemCategory.BUSINESS_INVALID_COMMAND, title, detail, context);
    }

    protected SocialProblem(String type, String title, String detail) {
        this(type, title, detail, null);
    }

    protected SocialProblem(String type, String title) {
        this(type, title, null, null);
    }
}
