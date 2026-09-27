-- Fin de défi : scores des deux runs, vainqueur et date de complétion.
ALTER TABLE challenge_entry
    ADD COLUMN IF NOT EXISTS challenger_score INTEGER;

ALTER TABLE challenge_entry
    ADD COLUMN IF NOT EXISTS challenged_score INTEGER;

ALTER TABLE challenge_entry
    ADD COLUMN IF NOT EXISTS winner_id VARCHAR(255);

ALTER TABLE challenge_entry
    ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;
