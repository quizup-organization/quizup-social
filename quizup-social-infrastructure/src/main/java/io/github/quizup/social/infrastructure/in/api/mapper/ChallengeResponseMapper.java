package io.github.quizup.social.infrastructure.in.api.mapper;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.infrastructure.in.api.response.ChallengeResponse;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.infrastructure.in.api.response.PageResponse;
import io.github.quizup.common.infrastructure.mapper.SearchResponseMapper;

import java.util.List;

/**
 * Mapper pour convertir Challenge (domaine) → ChallengeResponse (DTO)
 */
public final class ChallengeResponseMapper {

    private ChallengeResponseMapper() {
    }

    public static ChallengeResponse toResponse(Challenge challenge) {
        return new ChallengeResponse(
                challenge.challengeId(),
                challenge.challengerId(),
                challenge.challengedId(),
                challenge.topicId(),
                challenge.status(),
                challenge.createdAt(),
                challenge.acceptedAt(),
                challenge.declinedAt(),
                challenge.expiresAt()
        );
    }

    public static PageResponse<ChallengeResponse> toResponse(PageResult<Challenge> pageResult) {
        return SearchResponseMapper.toSearchResponse(pageResult, ChallengeResponseMapper::toResponse);
    }

    public static List<ChallengeResponse> toResponse(List<Challenge> challenges) {
        return challenges.stream()
                .map(ChallengeResponseMapper::toResponse)
                .toList();
    }
}

