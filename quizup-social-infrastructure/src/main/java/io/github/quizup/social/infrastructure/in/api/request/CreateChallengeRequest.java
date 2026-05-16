package io.github.quizup.social.infrastructure.in.api.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Requête de création d'un défi
 */
public record CreateChallengeRequest(
        @NotBlank(message = "L'identifiant du joueur défié est obligatoire")
        String challengedId,

        @NotBlank(message = "L'identifiant du thème est obligatoire")
        String topicId
) {
}

