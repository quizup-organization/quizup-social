package io.github.quizup.social.domain.port.out;

import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.social.domain.model.UserFollower;

import java.util.Optional;

public interface UserFollowerRepositoryPort {

    void save(UserFollower userFollower);

    Optional<UserFollower> findById(String followId);

    void deleteById(String followId);

    boolean exists(String followerId, String followedId);

    PageResult<UserFollower> findAll(SearchCriteria searchCriteria);
}
