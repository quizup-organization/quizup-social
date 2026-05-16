package io.github.quizup.social.domain.query;

import io.github.quizup.common.domain.model.search.FilterCriteria;
import io.github.quizup.common.domain.model.search.PageCriteria;
import io.github.quizup.common.domain.model.search.SortCriteria;
import io.github.quizup.common.domain.query.SearchQuery;

import java.util.List;

public interface FriendshipQuery {

	record SearchFriendshipQuery(
			List<FilterCriteria> filters,
			List<SortCriteria> sorts,
			PageCriteria page
	) implements FriendshipQuery, SearchQuery {
	}

	record FriendshipExistsQuery(String userId1, String userId2) implements FriendshipQuery {
	}

	record GetFriendshipsByUserQuery(String userId) implements FriendshipQuery {
	}
}

