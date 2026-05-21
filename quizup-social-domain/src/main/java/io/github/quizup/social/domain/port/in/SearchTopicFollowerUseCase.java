package io.github.quizup.social.domain.port.in;

import io.github.quizup.common.domain.model.search.FilterCriteria;
import io.github.quizup.common.domain.model.search.PageCriteria;
import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SortCriteria;
import io.github.quizup.social.domain.model.TopicFollower;
import io.github.quizup.social.domain.query.TopicFollowerQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchTopicFollowerUseCase {
    CompletableFuture<PageResult<TopicFollower>> search(TopicFollowerQuery.SearchTopicFollowerQuery query);

    default CompletableFuture<PageResult<TopicFollower>> search(
            List<FilterCriteria> filters,
            List<SortCriteria> sorts,
            PageCriteria page
    ) {
        return search(new TopicFollowerQuery.SearchTopicFollowerQuery(filters, sorts, page));
    }
}

