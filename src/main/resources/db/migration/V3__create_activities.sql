-- ═══════════════════════════════════════════════════════════════════
--  V3 — Tables du module Activity
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE activities.activities (
    id               VARCHAR(36)      PRIMARY KEY,
    organizer_id     VARCHAR(36)      NOT NULL,
    title            VARCHAR(200)     NOT NULL,
    description      TEXT,
    sport            VARCHAR(100)     NOT NULL,
    required_level   VARCHAR(30),
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    address          VARCHAR(300),
    city             VARCHAR(100)     NOT NULL,
    country          VARCHAR(100),
    scheduled_at     TIMESTAMPTZ      NOT NULL,
    max_participants INT              NOT NULL DEFAULT 10,
    status           VARCHAR(20)      NOT NULL DEFAULT 'OPEN',
    created_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activities_organizer   ON activities.activities (organizer_id);
CREATE INDEX idx_activities_sport       ON activities.activities (sport);
CREATE INDEX idx_activities_status      ON activities.activities (status);
CREATE INDEX idx_activities_scheduled   ON activities.activities (scheduled_at);
CREATE INDEX idx_activities_location    ON activities.activities (latitude, longitude);

-- ── activities.activity_participants ─────────────────────────────────────────

CREATE TABLE activities.activity_participants (
    id          BIGSERIAL    PRIMARY KEY,
    activity_id VARCHAR(36)  NOT NULL REFERENCES activities.activities (id) ON DELETE CASCADE,
    user_id     VARCHAR(36)  NOT NULL,
    joined_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (activity_id, user_id)
);

CREATE INDEX idx_participants_activity ON activities.activity_participants (activity_id);
CREATE INDEX idx_participants_user     ON activities.activity_participants (user_id);
