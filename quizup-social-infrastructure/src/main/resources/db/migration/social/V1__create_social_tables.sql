-- V1: Schema initial complet du module social
--
-- Defis (challenges) 1v1 et abonnements (topics + joueurs), modele « follow » unidirectionnel.
-- Tables : challenge_entry, topic_follower, user_follower.

CREATE TABLE challenge_entry (
    challenge_id       VARCHAR(255) NOT NULL,
    challenger_id      VARCHAR(255) NOT NULL,
    challenged_id      VARCHAR(255) NOT NULL,
    topic_id           VARCHAR(255) NOT NULL,
    game_id            VARCHAR(255),
    challenger_game_id VARCHAR(255),
    challenged_game_id VARCHAR(255),
    replay_game_id     VARCHAR(255),
    status             VARCHAR(20)  NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    accepted_at        TIMESTAMP,
    declined_at        TIMESTAMP,
    expires_at         TIMESTAMP    NOT NULL,
    PRIMARY KEY (challenge_id)
);

CREATE INDEX idx_challenge_entry_challenger ON challenge_entry (challenger_id);
CREATE INDEX idx_challenge_entry_challenged ON challenge_entry (challenged_id);
CREATE INDEX idx_challenge_entry_status ON challenge_entry (status);
CREATE INDEX idx_challenge_entry_topic ON challenge_entry (topic_id);

CREATE TABLE IF NOT EXISTS topic_follower (
    follow_id VARCHAR(255) PRIMARY KEY,
    topic_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    followed_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_topic_follower_topic_user UNIQUE (topic_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_topic_follower_topic ON topic_follower(topic_id);
CREATE INDEX IF NOT EXISTS idx_topic_follower_user ON topic_follower(user_id);
CREATE INDEX IF NOT EXISTS idx_topic_follower_followed_at ON topic_follower(followed_at);

CREATE TABLE IF NOT EXISTS user_follower (
    follow_id VARCHAR(255) PRIMARY KEY,
    follower_id VARCHAR(255) NOT NULL,
    followed_id VARCHAR(255) NOT NULL,
    followed_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_user_follower_follower_followed UNIQUE (follower_id, followed_id)
);

CREATE INDEX IF NOT EXISTS idx_user_follower_follower ON user_follower(follower_id);
CREATE INDEX IF NOT EXISTS idx_user_follower_followed ON user_follower(followed_id);
CREATE INDEX IF NOT EXISTS idx_user_follower_followed_at ON user_follower(followed_at);
