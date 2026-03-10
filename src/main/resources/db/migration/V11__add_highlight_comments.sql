-- ═══════════════════════════════════════════════════════════════════
--  V9 — Highlight Comments
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE highlight.comments (
    id           VARCHAR(36)  PRIMARY KEY,
    highlight_id VARCHAR(36)  NOT NULL REFERENCES highlight.highlights (id) ON DELETE CASCADE,
    author_id    VARCHAR(36)  NOT NULL REFERENCES users.users (id),
    content      VARCHAR(300) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_highlight ON highlight.comments (highlight_id, created_at DESC);
