-- V1__create_social_tables.sql

-- Table pour les demandes d'ami
CREATE TABLE friend_request (
    request_id VARCHAR(255) PRIMARY KEY,
    sender_id VARCHAR(255) NOT NULL,
    target_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,  -- PENDING, ACCEPTED, REJECTED
    sent_at TIMESTAMP NOT NULL,
    responded_at TIMESTAMP,
    version BIGINT
);

-- Index partiel : une seule demande PENDING par paire (sender, target) à la fois.
-- Les demandes ACCEPTED/REJECTED ne bloquent pas une nouvelle demande future.
CREATE UNIQUE INDEX uq_friend_request_pending
    ON friend_request(sender_id, target_id)
    WHERE status = 'PENDING';

CREATE INDEX idx_fr_target_status ON friend_request(target_id, status);
CREATE INDEX idx_fr_sender_status ON friend_request(sender_id, status);

-- Table pour les amitiés (unidirectionnelle simplifiée)
-- Une seule entrée par paire avec userId1 < userId2 (lexicographiquement)
CREATE TABLE friendship (
    friendship_id VARCHAR(255) PRIMARY KEY,
    user_id_1 VARCHAR(255) NOT NULL,
    user_id_2 VARCHAR(255) NOT NULL,
    friends_since TIMESTAMP NOT NULL,
    version BIGINT,
    CONSTRAINT uq_friendship UNIQUE (user_id_1, user_id_2)
);

CREATE INDEX idx_friendship_user1 ON friendship(user_id_1);
CREATE INDEX idx_friendship_user2 ON friendship(user_id_2);
