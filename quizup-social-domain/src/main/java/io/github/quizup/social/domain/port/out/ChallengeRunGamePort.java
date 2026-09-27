package io.github.quizup.social.domain.port.out;

/**
 * Port sortant — vérification qu'une partie est bien un run asynchrone appartenant au joueur,
 * sur le sujet du défi (validation du {@code gameId} à l'enregistrement d'un run).
 */
public interface ChallengeRunGamePort {

    boolean isAsyncRunOwnedBy(String gameId, String playerId, String topicId);
}
