package io.github.quizup.social.domain.port.out;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.social.domain.model.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepositoryPort {

    void save(Friendship friendship);

    Optional<Friendship> findById(String friendshipId);

    void deleteById(String friendshipId);

    List<Friendship> findByUserId(String userId);

    Optional<Friendship> findByUserIds(String userId1, String userId2);

    boolean existsBetweenUsers(String userId1, String userId2);

    PageResult<Friendship> findAll(SearchCriteria criteria);
}

