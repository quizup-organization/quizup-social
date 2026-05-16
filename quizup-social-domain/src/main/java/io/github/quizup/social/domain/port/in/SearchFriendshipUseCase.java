package io.github.quizup.social.domain.port.in;

import io.github.quizup.common.domain.model.search.FilterCriteria;
import io.github.quizup.common.domain.model.search.PageCriteria;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SortCriteria;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.domain.query.FriendshipQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchFriendshipUseCase {

    CompletableFuture<PageResult<Friendship>> search(FriendshipQuery.SearchFriendshipQuery query);

    default CompletableFuture<PageResult<Friendship>> search(List<FilterCriteria> filters,
                                                             List<SortCriteria> sorts,
                                                             PageCriteria page) {
        return search(
                new FriendshipQuery.SearchFriendshipQuery(
                        filters,
                        sorts,
                        page
                )
        );
    }
}

