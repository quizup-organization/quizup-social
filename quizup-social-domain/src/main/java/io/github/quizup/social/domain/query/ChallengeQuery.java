package io.github.quizup.social.domain.query;

import io.github.quizup.common.domain.model.search.FilterCriteria;
import io.github.quizup.common.domain.model.search.PageCriteria;
import io.github.quizup.common.domain.model.search.SortCriteria;
import io.github.quizup.common.domain.query.SearchQuery;

import java.util.List;

public interface ChallengeQuery {

    record SearchChallengeQuery(
            List<FilterCriteria> filters,
            List<SortCriteria> sorts,
            PageCriteria page
    ) implements ChallengeQuery, SearchQuery {
    }

    record GetChallengeByIdQuery(
            String challengeId
    ) implements ChallengeQuery {
    }
}
