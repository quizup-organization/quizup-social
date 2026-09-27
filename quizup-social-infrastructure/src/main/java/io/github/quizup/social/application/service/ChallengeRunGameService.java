package io.github.quizup.social.application.service;

import io.github.quizup.game.domain.model.GameMode;
import io.github.quizup.game.domain.model.GameRunInfo;
import io.github.quizup.game.domain.query.GameQuery;
import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.social.domain.port.out.ChallengeRunGamePort;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Validation synchrone du {@code gameId} d'un run : la partie doit exister, être asynchrone,
 * appartenir au joueur et porter le sujet du défi. Requête dédiée et minimale
 * ({@link GameQuery.GetGameRunInfoQuery}) plutôt qu'une lecture de la projection game complète.
 *
 * <p>La commande de création de partie et l'enregistrement du run s'enchaînent côté client :
 * la projection game peut être en retard de quelques millisecondes. En cas de partie absente,
 * on retente brièvement (borné) ; une partie présente mais non conforme est refusée immédiatement.</p>
 */
@Service
public class ChallengeRunGameService implements ChallengeRunGamePort {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeRunGameService.class);
    private static final int MAX_ATTEMPTS = 5;
    private static final long RETRY_DELAY_MILLIS = 200;

    private final QueryGateway queryGateway;

    public ChallengeRunGameService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public boolean isAsyncRunOwnedBy(String gameId, String playerId, String topicId) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            Optional<GameRunInfo> run = findRun(gameId);
            if (run.isPresent()) {
                return matches(run.get(), playerId, topicId);
            }
            if (attempt < MAX_ATTEMPTS) {
                sleep(RETRY_DELAY_MILLIS);
            }
        }
        logger.warn("Run introuvable après {} tentatives: gameId={}", MAX_ATTEMPTS, gameId);
        return false;
    }

    private Optional<GameRunInfo> findRun(String gameId) {
        CompletableFuture<Optional<GameRunInfo>> query = queryGateway.query(
                new GameQuery.GetGameRunInfoQuery(gameId),
                QueryResponseTypes.optionalInstanceOf(GameRunInfo.class));
        return query.join();
    }

    private static boolean matches(GameRunInfo run, String playerId, String topicId) {
        return run.mode() == GameMode.ASYNC
                && playerId.equals(run.player1Id())
                && topicId.equals(run.topicId());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
