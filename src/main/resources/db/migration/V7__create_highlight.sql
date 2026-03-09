-- ═══════════════════════════════════════════════════════════════════
--  V7 — Module Highlight : pic/action of the day
-- ═══════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS highlight;

-- ── highlight.highlights ──────────────────────────────────────────────────────

CREATE TABLE highlight.highlights (
    id                  VARCHAR(36)     PRIMARY KEY,
    publisher_id        VARCHAR(36)     NOT NULL REFERENCES users.users (id),
    media_url           VARCHAR(1000)   NOT NULL,
    media_type          VARCHAR(10)     NOT NULL CHECK (media_type IN ('PHOTO', 'VIDEO')),
    caption             VARCHAR(500),
    sport               VARCHAR(100),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    like_count          INT             NOT NULL DEFAULT 0,
    is_highlight_of_day BOOLEAN         NOT NULL DEFAULT FALSE,
    published_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_highlights_publisher   ON highlight.highlights (publisher_id);
CREATE INDEX idx_highlights_sport       ON highlight.highlights (sport);
CREATE INDEX idx_highlights_hot         ON highlight.highlights (like_count DESC, published_at DESC);
CREATE INDEX idx_highlights_of_day      ON highlight.highlights (is_highlight_of_day) WHERE is_highlight_of_day = TRUE;

-- ── highlight.likes ───────────────────────────────────────────────────────────

CREATE TABLE highlight.likes (
    highlight_id    VARCHAR(36)     NOT NULL REFERENCES highlight.highlights (id) ON DELETE CASCADE,
    user_id         VARCHAR(36)     NOT NULL REFERENCES users.users (id),
    liked_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (highlight_id, user_id)
);

CREATE INDEX idx_likes_user ON highlight.likes (user_id);

-- ── highlight.highlight_of_day ────────────────────────────────────────────────

CREATE TABLE highlight.highlight_of_day (
    date            DATE            PRIMARY KEY,
    highlight_id    VARCHAR(36)     NOT NULL REFERENCES highlight.highlights (id),
    selected_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
