package io.github.quizup.social.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.theme.domain.query.TopicQuery;
import io.github.quizup.social.domain.port.out.TopicRepositoryPort;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

@Service
public class TopicService implements TopicRepositoryPort {

    private final QueryGateway queryGateway;

    public TopicService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public boolean existsById(String topicId) {
        return queryGateway.query(
                new TopicQuery.TopicExistsByIdQuery(topicId),
                QueryResponseTypes.instanceOf(Boolean.class)
        ).join();
    }
}
