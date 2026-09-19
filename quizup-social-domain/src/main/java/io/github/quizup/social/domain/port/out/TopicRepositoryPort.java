package io.github.quizup.social.domain.port.out;

public interface TopicRepositoryPort {
    boolean existsById(String topicId);
}

