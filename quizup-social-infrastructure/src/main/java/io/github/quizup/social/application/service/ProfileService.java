package io.github.quizup.social.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.profile.domain.query.ProfileQuery;
import io.github.quizup.social.domain.port.out.ProfileRepositoryPort;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

@Service
public class ProfileService implements ProfileRepositoryPort {

    private final QueryGateway queryGateway;

    public ProfileService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public boolean existsById(String userId) {
        return queryGateway.query(
                new ProfileQuery.ProfileExistsByIdQuery(userId),
                QueryResponseTypes.instanceOf(Boolean.class)
        ).join();
    }
}
