-- ═══════════════════════════════════════════════════════════════════
--  V5 — Module Rating : notation des joueurs post-match
-- ═══════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS rating;

-- ── rating.player_ratings ─────────────────────────────────────────────────────

CREATE TABLE rating.player_ratings (
    id              VARCHAR(36)     PRIMARY KEY,
    activity_id     VARCHAR(36)     NOT NULL,
    rater_id        VARCHAR(36)     NOT NULL REFERENCES users.users (id),
    rated_user_id   VARCHAR(36)     NOT NULL REFERENCES users.users (id),
    behavior        SMALLINT        NOT NULL CHECK (behavior BETWEEN 1 AND 5),
    technicality    SMALLINT        NOT NULL CHECK (technicality BETWEEN 1 AND 5),
    teamwork        SMALLINT        NOT NULL CHECK (teamwork BETWEEN 1 AND 5),
    level           VARCHAR(30)     NOT NULL,
    is_man_of_match BOOLEAN         NOT NULL DEFAULT FALSE,
    comment         VARCHAR(500),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (activity_id, rater_id, rated_user_id)
);

CREATE INDEX idx_ratings_activity     ON rating.player_ratings (activity_id);
CREATE INDEX idx_ratings_rated_user   ON rating.player_ratings (rated_user_id);
CREATE INDEX idx_ratings_rater        ON rating.player_ratings (rater_id);

-- ── rating.player_stats ───────────────────────────────────────────────────────

CREATE TABLE rating.player_stats (
    user_id             VARCHAR(36)     PRIMARY KEY REFERENCES users.users (id),
    sport               VARCHAR(100)    NOT NULL,
    total_ratings       INT             NOT NULL DEFAULT 0,
    avg_behavior        NUMERIC(3,2)    NOT NULL DEFAULT 0,
    avg_technicality    NUMERIC(3,2)    NOT NULL DEFAULT 0,
    avg_teamwork        NUMERIC(3,2)    NOT NULL DEFAULT 0,
    win_rate            NUMERIC(5,2)    NOT NULL DEFAULT 0,
    man_of_match_count  INT             NOT NULL DEFAULT 0,
    pro_score           INT             NOT NULL DEFAULT 0 CHECK (pro_score BETWEEN 0 AND 100),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_player_stats_sport    ON rating.player_stats (sport);
CREATE INDEX idx_player_stats_score    ON rating.player_stats (pro_score DESC);

-- ── rating.leaderboard_cache ──────────────────────────────────────────────────

CREATE TABLE rating.leaderboard_cache (
    sport       VARCHAR(100)    NOT NULL,
    rank        INT             NOT NULL,
    user_id     VARCHAR(36)     NOT NULL REFERENCES users.users (id),
    score       INT             NOT NULL,
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (sport, rank)
);

CREATE INDEX idx_leaderboard_sport ON rating.leaderboard_cache (sport, score DESC);
