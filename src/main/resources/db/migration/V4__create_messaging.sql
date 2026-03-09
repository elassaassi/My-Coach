-- ═══════════════════════════════════════════════════════════════════
--  V4 — Tables du module Messaging
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE messaging.conversations (
    id                    VARCHAR(36)  PRIMARY KEY,
    participant_a         VARCHAR(36)  NOT NULL,
    participant_b         VARCHAR(36)  NOT NULL,
    last_message_preview  VARCHAR(100),
    last_message_at       TIMESTAMPTZ,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (participant_a, participant_b)
);

CREATE INDEX idx_conversations_a ON messaging.conversations (participant_a);
CREATE INDEX idx_conversations_b ON messaging.conversations (participant_b);

-- ── messaging.messages ────────────────────────────────────────────────────────

CREATE TABLE messaging.messages (
    id               VARCHAR(36)  PRIMARY KEY,
    conversation_id  VARCHAR(36)  NOT NULL REFERENCES messaging.conversations (id) ON DELETE CASCADE,
    sender_id        VARCHAR(36)  NOT NULL,
    content          VARCHAR(2000) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'SENT',
    sent_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_conversation ON messaging.messages (conversation_id);
CREATE INDEX idx_messages_sent_at      ON messaging.messages (conversation_id, sent_at DESC);
