-- ═══════════════════════════════════════════════════════════════════
--  V10 — Chat de groupe par activité
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE activities.activity_messages (
    id           VARCHAR(36)   PRIMARY KEY,
    activity_id  VARCHAR(36)   NOT NULL REFERENCES activities.activities(id) ON DELETE CASCADE,
    sender_id    VARCHAR(36)   NOT NULL,
    content      TEXT          NOT NULL CHECK (char_length(content) <= 2000),
    sent_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activity_messages_activity ON activities.activity_messages (activity_id, sent_at DESC);
