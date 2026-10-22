package io.github.quizup.social.application.handler.event;

import io.github.quizup.axon.autoconfigure.erasure.AxonEventStoreErasureService;
import io.github.quizup.identity.domain.event.UserEvent.UserAccountDeletedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Supprime les relations de suivi d'un compte supprimé. */
@Component
@ProcessingGroup("account-deletion-social")
public class AccountDeletionHandler {

    private final JdbcTemplate jdbcTemplate;
    private final AxonEventStoreErasureService eventStoreErasureService;

    public AccountDeletionHandler(JdbcTemplate jdbcTemplate,
                                  AxonEventStoreErasureService eventStoreErasureService) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventStoreErasureService = eventStoreErasureService;
    }

    @EventHandler
    @Transactional
    public void on(UserAccountDeletedEvent event) {
        String userId = event.userId();
        java.util.List<String> followIds = jdbcTemplate.queryForList("""
                SELECT follow_id FROM topic_follower WHERE user_id = ?
                UNION
                SELECT follow_id FROM user_follower WHERE follower_id = ? OR followed_id = ?
                """, String.class, userId, userId, userId);
        jdbcTemplate.update("DELETE FROM topic_follower WHERE user_id = ?", userId);
        jdbcTemplate.update(
                "DELETE FROM user_follower WHERE follower_id = ? OR followed_id = ?",
                userId, userId);
        eventStoreErasureService.eraseAggregates(followIds);
    }
}
