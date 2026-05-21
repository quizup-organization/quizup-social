package io.github.quizup.social.application.service;

import io.github.quizup.identity.domain.query.UserQuery;
import io.github.quizup.social.domain.port.out.UserPort;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserPort {

    private final QueryGateway queryGateway;

    public UserService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public boolean existsById(String userId) {
        return queryGateway.query(
                new UserQuery.UserExistsByIdQuery(userId),
                ResponseTypes.instanceOf(Boolean.class)
        ).join();
    }
}

