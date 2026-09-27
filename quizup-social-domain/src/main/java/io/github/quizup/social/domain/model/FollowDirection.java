package io.github.quizup.social.domain.model;

/**
 * Sens de lecture d'une liste d'abonnements.
 */
public enum FollowDirection {
    /** Joueurs suivis par l'utilisateur ({@code followerId = userId}). */
    FOLLOWING,
    /** Abonnés de l'utilisateur ({@code followedId = userId}). */
    FOLLOWERS
}
