package io.github.quizup.social.application.service;

import io.github.quizup.theme.domain.query.TopicQuery;
import io.github.quizup.social.domain.port.out.TopicPort;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

@Service
public class TopicService implements TopicPort {

    private final QueryGateway queryGateway;

    public TopicService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public boolean existsById(String topicId) {
        return queryGateway.query(
                new TopicQuery.TopicExistsByIdQuery(topicId),
                ResponseTypes.instanceOf(Boolean.class)
        ).join();
    }
}
