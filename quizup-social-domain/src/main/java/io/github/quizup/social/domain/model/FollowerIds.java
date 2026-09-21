package io.github.quizup.social.domain.model;

/**
 * Identifiants déterministes des agrégats de suivi : l'id encode la clé naturelle
 * ({@code <followed-side>:<...>}), ce qui garantit l'unicité d'un suivi sans dépendre
 * d'une lecture éventuellement cohérente du read model.
 *
 * <p>Axon sérialise les commandes par id d'agrégat : deux follows concurrents sur la même
 * paire ciblent donc le même agrégat et le second est idempotent (pas de doublon).</p>
 */
public final class FollowerIds {

    private FollowerIds() {
    }

    /** Suivi d'un sujet : {@code userId + ":" + topicId}. */
    public static String topic(String topicId, String userId) {
        return userId + ":" + topicId;
    }

    /** Suivi d'un joueur : {@code followerId + ":" + followedId}. */
    public static String user(String followerId, String followedId) {
        return followerId + ":" + followedId;
    }
}
