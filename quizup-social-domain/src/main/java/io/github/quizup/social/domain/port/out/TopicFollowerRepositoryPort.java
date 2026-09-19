package io.github.quizup.social.domain.port.out;

import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.microservice.core.domain.model.search.SearchCriteria;
import io.github.quizup.social.domain.model.TopicFollower;

import java.util.Optional;

public interface TopicFollowerRepositoryPort {

    void save(TopicFollower topicFollower);

    Optional<TopicFollower> findById(String followId);

    void deleteById(String followId);

    boolean exists(String topicId, String userId);

    PageResult<TopicFollower> findAll(SearchCriteria searchCriteria);
}
