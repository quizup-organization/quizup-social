-- V2: création des tables challenge dans le service social

CREATE TABLE challenge_entry (
    challenge_id   VARCHAR(255) NOT NULL,
    challenger_id  VARCHAR(255) NOT NULL,
    challenged_id  VARCHAR(255) NOT NULL,
    topic_id       VARCHAR(255) NOT NULL,
    game_id        VARCHAR(255),
    status         VARCHAR(20)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    accepted_at    TIMESTAMP,
    declined_at    TIMESTAMP,
    expires_at     TIMESTAMP    NOT NULL,
    PRIMARY KEY (challenge_id)
);

CREATE INDEX idx_challenge_entry_challenger ON challenge_entry (challenger_id);
CREATE INDEX idx_challenge_entry_challenged ON challenge_entry (challenged_id);
CREATE INDEX idx_challenge_entry_status ON challenge_entry (status);
CREATE INDEX idx_challenge_entry_topic ON challenge_entry (topic_id);

