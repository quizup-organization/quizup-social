package io.github.quizup.social.domain.model;

import io.github.quizup.microservice.core.domain.model.i18n.Language;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Union des langues non nulles d'un ensemble de profils de défi.
 */
public final class ChallengeLanguages {

    private ChallengeLanguages() {
    }

    public static Set<Language> of(ChallengeProfile... profiles) {
        Set<Language> languages = new HashSet<>();
        Arrays.stream(profiles)
                .filter(profile -> profile != null && profile.language() != null)
                .forEach(profile -> languages.add(profile.language()));
        return languages;
    }
}
