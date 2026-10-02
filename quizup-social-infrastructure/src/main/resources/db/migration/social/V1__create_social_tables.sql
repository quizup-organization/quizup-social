-- V1: Schema initial du module social
--
-- Abonnements (topics + joueurs), modele « follow » unidirectionnel.
-- Tables : topic_follower, user_follower.

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
