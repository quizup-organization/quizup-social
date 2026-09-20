package io.github.quizup.social.domain.port.out;

/**
 * Port sortant des KPI métier du social (abonnements + défis).
 *
 * <p>Implémenté en infrastructure avec Micrometer. Types JDK uniquement (règle hexagonale).
 */
public interface SocialMetricsPort {

    void topicFollowed();

    void topicUnfollowed();

    void userFollowed();

    void userUnfollowed();

    void challengeCreated(String topicId);

    void challengeAccepted();

    void challengeDeclined();

    void challengeExpired();

    void challengeRunRegistered();
}
