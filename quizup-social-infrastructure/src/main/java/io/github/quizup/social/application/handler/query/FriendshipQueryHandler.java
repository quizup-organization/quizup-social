package io.github.quizup.social.application.handler.query;

import io.github.quizup.common.domain.model.search.PageResult;
import io.github.quizup.social.domain.query.FriendshipQuery;
import io.github.quizup.social.domain.model.Friendship;
import io.github.quizup.social.domain.port.out.FriendshipRepositoryPort;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FriendshipQueryHandler — Gère les queries liées aux amitiés établies.
 */
@Component
public class FriendshipQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipQueryHandler.class);

    private final FriendshipRepositoryPort friendshipRepositoryPort;

    public FriendshipQueryHandler(FriendshipRepositoryPort friendshipRepositoryPort) {
        this.friendshipRepositoryPort = friendshipRepositoryPort;
    }

    @QueryHandler
    public boolean handle(FriendshipQuery.FriendshipExistsQuery query) {
        logger.debug("FriendshipExistsQuery: userId1={}, userId2={}", query.userId1(), query.userId2());
        return friendshipRepositoryPort.existsBetweenUsers(query.userId1(), query.userId2());
    }

    @QueryHandler
    public List<Friendship> handle(FriendshipQuery.GetFriendshipsByUserQuery query) {
        logger.debug("GetFriendshipsByUserQuery: userId={}", query.userId());
        return friendshipRepositoryPort.findByUserId(query.userId());
    }

    @QueryHandler
    public PageResult<Friendship> handle(FriendshipQuery.SearchFriendshipQuery query) {
        return friendshipRepositoryPort.findAll(query);
    }
}

