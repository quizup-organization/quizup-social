package io.github.quizup.social.domain.port.out;

/**
 * Port sortant — existence d'un profil (validation d'un suivi de joueur).
 */
public interface ProfileRepositoryPort {

    boolean existsById(String identifier);
}
