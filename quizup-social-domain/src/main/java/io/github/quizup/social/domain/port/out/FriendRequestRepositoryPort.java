package io.github.quizup.social.domain.port.out;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.common.domain.model.search.SearchCriteria;
import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.model.FriendRequestStatus;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepositoryPort {
    void save(FriendRequest friendRequest);

    Optional<FriendRequest> findById(String requestId);

    void deleteById(String requestId);

    List<FriendRequest> findByTargetIdAndStatus(String targetId, FriendRequestStatus status);

    List<FriendRequest> findBySenderIdAndStatus(String senderId, FriendRequestStatus status);

    Optional<FriendRequest> findBySenderIdAndTargetId(String senderId, String targetId);

    PageResult<FriendRequest> findAll(SearchCriteria criteria);
}

