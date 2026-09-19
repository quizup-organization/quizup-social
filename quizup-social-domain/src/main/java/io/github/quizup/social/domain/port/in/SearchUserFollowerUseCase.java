package io.github.quizup.social.domain.port.in;

import io.github.quizup.microservice.core.domain.model.search.FilterCriteria;
import io.github.quizup.microservice.core.domain.model.search.PageCriteria;
import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.microservice.core.domain.model.search.SortCriteria;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.query.UserFollowerQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchUserFollowerUseCase {
    CompletableFuture<PageResult<UserFollower>> search(UserFollowerQuery.SearchUserFollowerQuery query);

    default CompletableFuture<PageResult<UserFollower>> search(
            List<FilterCriteria> filters,
            List<SortCriteria> sorts,
            PageCriteria page
    ) {
        return search(new UserFollowerQuery.SearchUserFollowerQuery(filters, sorts, page));
    }
}
