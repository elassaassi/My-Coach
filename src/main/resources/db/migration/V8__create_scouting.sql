-- ═══════════════════════════════════════════════════════════════════
--  V8 — Module Scouting : talents & recruteurs
-- ═══════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS scouting;

-- ── scouting.talent_profiles ──────────────────────────────────────────────────

CREATE TABLE scouting.talent_profiles (
    user_id             VARCHAR(36)     PRIMARY KEY REFERENCES users.users (id),
    sport               VARCHAR(100)    NOT NULL,
    pro_score           INT             NOT NULL DEFAULT 0 CHECK (pro_score BETWEEN 0 AND 100),
    is_visible          BOOLEAN         NOT NULL DEFAULT TRUE,
    recruitment_status  VARCHAR(30)     NOT NULL DEFAULT 'OPEN',
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_talent_sport       ON scouting.talent_profiles (sport);
CREATE INDEX idx_talent_score       ON scouting.talent_profiles (pro_score DESC);
CREATE INDEX idx_talent_visible     ON scouting.talent_profiles (is_visible) WHERE is_visible = TRUE;

-- ── scouting.recruiter_profiles ───────────────────────────────────────────────

CREATE TABLE scouting.recruiter_profiles (
    id              VARCHAR(36)     PRIMARY KEY,
    user_id         VARCHAR(36)     NOT NULL UNIQUE REFERENCES users.users (id),
    organization    VARCHAR(200)    NOT NULL,
    target_level    VARCHAR(30)     NOT NULL DEFAULT 'SEMI_PRO',
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE scouting.recruiter_sports (
    recruiter_id    VARCHAR(36)     NOT NULL REFERENCES scouting.recruiter_profiles (id) ON DELETE CASCADE,
    sport           VARCHAR(100)    NOT NULL,
    PRIMARY KEY (recruiter_id, sport)
);

-- ── scouting.scouting_interests ───────────────────────────────────────────────

CREATE TABLE scouting.scouting_interests (
    id              VARCHAR(36)     PRIMARY KEY,
    recruiter_id    VARCHAR(36)     NOT NULL REFERENCES scouting.recruiter_profiles (id),
    talent_id       VARCHAR(36)     NOT NULL REFERENCES scouting.talent_profiles (user_id),
    status          VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    note            VARCHAR(500),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (recruiter_id, talent_id)
);

CREATE INDEX idx_interests_recruiter ON scouting.scouting_interests (recruiter_id);
CREATE INDEX idx_interests_talent    ON scouting.scouting_interests (talent_id);
CREATE INDEX idx_interests_status    ON scouting.scouting_interests (status);
