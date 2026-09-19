package io.github.quizup.social.infrastructure.in.api.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO d'enregistrement du run asynchrone d'un participant (jeu en différé).
 */
public record RegisterChallengeRunRequest(
        @NotBlank String gameId
) {
}
