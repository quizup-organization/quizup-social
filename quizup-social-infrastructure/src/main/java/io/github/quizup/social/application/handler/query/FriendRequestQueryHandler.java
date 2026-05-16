package io.github.quizup.social.application.handler.query;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.social.domain.model.FriendRequestDirection;
import io.github.quizup.social.domain.model.FriendRequest;
import io.github.quizup.social.domain.model.FriendRequestStatus;
import io.github.quizup.social.domain.port.out.FriendRequestRepositoryPort;
import io.github.quizup.social.domain.query.FriendRequestQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FriendRequestQueryHandler — Gère les queries liées aux demandes d'ami.
 */
@Component
public class FriendRequestQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(FriendRequestQueryHandler.class);

    private final FriendRequestRepositoryPort friendRequestRepositoryPort;

    public FriendRequestQueryHandler(FriendRequestRepositoryPort friendRequestRepositoryPort) {
        this.friendRequestRepositoryPort = friendRequestRepositoryPort;
    }

    @QueryHandler
    public List<FriendRequest> handle(FriendRequestQuery.GetFriendRequestsQuery query) {
        logger.debug("GetFriendRequestsQuery: userId={}, direction={}", query.userId(), query.direction());

        return switch (query.direction()) {
            case RECEIVED -> friendRequestRepositoryPort.findByTargetIdAndStatus(query.userId(), FriendRequestStatus.PENDING);
            case SENT -> friendRequestRepositoryPort.findBySenderIdAndStatus(query.userId(), FriendRequestStatus.PENDING);
        };
    }

    @QueryHandler
    public PageResult<FriendRequest> handle(FriendRequestQuery.SearchFriendRequestQuery query){
        return friendRequestRepositoryPort.findAll(query);
    }
}

