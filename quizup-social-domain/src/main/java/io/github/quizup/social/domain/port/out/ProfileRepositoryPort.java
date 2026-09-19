package io.github.quizup.social.domain.port.out;

import io.github.quizup.social.domain.model.ChallengeProfile;

public interface ProfileRepositoryPort {

    boolean existsById(String identifier);

    ChallengeProfile getById(String identifier);
}

