CREATE TABLE IF NOT EXISTS topic_follower (
    follow_id VARCHAR(255) PRIMARY KEY,
    topic_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    followed_at TIMESTAMP NOT NULL,
    unfollowed_at TIMESTAMP,
    CONSTRAINT uq_topic_follower_topic_user UNIQUE (topic_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_topic_follower_topic ON topic_follower(topic_id);
CREATE INDEX IF NOT EXISTS idx_topic_follower_user ON topic_follower(user_id);
CREATE INDEX IF NOT EXISTS idx_topic_follower_followed_at ON topic_follower(followed_at);

