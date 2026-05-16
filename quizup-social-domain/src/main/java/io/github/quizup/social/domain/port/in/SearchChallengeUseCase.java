package io.github.quizup.social.domain.port.in;

import io.github.quizup.social.domain.model.Challenge;
import io.github.quizup.social.domain.query.ChallengeQuery;
import io.github.quizup.common.domain.model.search.FilterCriteria;
import io.github.quizup.common.domain.model.search.PageCriteria;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SortCriteria;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchChallengeUseCase {

    CompletableFuture<PageResult<Challenge>> search(ChallengeQuery.SearchChallengeQuery query);

    default CompletableFuture<PageResult<Challenge>> search(List<FilterCriteria> filters,
                                                            List<SortCriteria> sorts,
                                                            PageCriteria page) {
        return search(
                new ChallengeQuery.SearchChallengeQuery(
                        filters,
                        sorts,
                        page
                )
        );
    }
}

