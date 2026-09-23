package io.github.quizup.social.domain.query;

import io.github.quizup.microservice.core.domain.model.search.FilterCriteria;
import io.github.quizup.microservice.core.domain.model.search.PageCriteria;
import io.github.quizup.microservice.core.domain.model.search.SortCriteria;
import io.github.quizup.microservice.core.domain.query.SearchQuery;

import java.util.List;

public interface UserFollowerQuery {

    record SearchUserFollowerQuery(
            List<FilterCriteria> filters,
            List<SortCriteria> sorts,
            PageCriteria page
    ) implements UserFollowerQuery, SearchQuery {
    }

    record GetUserFollowerByIdQuery(
            String followId
    ) implements UserFollowerQuery {
    }
}
