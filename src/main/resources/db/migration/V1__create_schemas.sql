-- ═══════════════════════════════════════════════════════════════════
--  V1 — Création des schémas et tables du module User
-- ═══════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS matching;
CREATE SCHEMA IF NOT EXISTS activities;
CREATE SCHEMA IF NOT EXISTS messaging;

-- ── users.users ──────────────────────────────────────────────────────────────

CREATE TABLE users.users (
    id              VARCHAR(36) PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    avatar_url      VARCHAR(500),
    status          VARCHAR(30)  NOT NULL DEFAULT 'PENDING_VERIFICATION',
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    city            VARCHAR(100),
    country         VARCHAR(100),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email       ON users.users (email);
CREATE INDEX idx_users_status      ON users.users (status);
CREATE INDEX idx_users_location    ON users.users (latitude, longitude)
    WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- ── users.user_sport_levels ──────────────────────────────────────────────────

CREATE TABLE users.user_sport_levels (
    id               BIGSERIAL PRIMARY KEY,
    user_id          VARCHAR(36) NOT NULL REFERENCES users.users (id) ON DELETE CASCADE,
    sport            VARCHAR(100) NOT NULL,
    proficiency      VARCHAR(30)  NOT NULL,
    years_experience INT          NOT NULL DEFAULT 0,
    UNIQUE (user_id, sport)
);

CREATE INDEX idx_sport_levels_user  ON users.user_sport_levels (user_id);
CREATE INDEX idx_sport_levels_sport ON users.user_sport_levels (sport);
