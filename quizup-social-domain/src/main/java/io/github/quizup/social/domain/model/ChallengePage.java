package io.github.quizup.social.domain.model;

import lombok.Builder;

import java.util.List;

/**
 * Page de défis d'un joueur (query dédiée, sans {@code SearchRequest}).
 */
@Builder(toBuilder = true)
public record ChallengePage(
        List<Challenge> challenges,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
