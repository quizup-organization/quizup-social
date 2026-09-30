package io.github.quizup.social.domain.model;

import io.github.quizup.microservice.core.domain.model.i18n.Language;

public record ChallengeProfile(String id, String name, Language language) {
}
