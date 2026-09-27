package io.github.quizup.social.domain.model;

/**
 * Compteurs d'abonnements d'un joueur (source de vérité : {@code quizup-social}).
 */
public record UserFollowCounts(long following, long followers) {
}
