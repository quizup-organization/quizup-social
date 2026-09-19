package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.time.Instant;

/**
 * Challenge domain model — représentation immuable d'un défi.
 *
 * <p>{@code gameId} porte la partie synchrone créée à l'acceptation. Pour un duel asynchrone,
 * {@code challengerGameId} / {@code challengedGameId} portent les runs enregistrés par chaque
 * joueur (le second rejouant le premier).</p>
 */
@Builder(toBuilder = true)
public record Challenge(
        String challengeId,
        String challengerId,
        String challengedId,
        String topicId,
        String gameId,
        String challengerGameId,
        String challengedGameId,
        String replayGameId,
        ChallengeStatus status,
        Instant createdAt,
        Instant acceptedAt,
        Instant declinedAt,
        Instant expiresAt
) {
}
