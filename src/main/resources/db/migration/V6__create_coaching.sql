-- ═══════════════════════════════════════════════════════════════════
--  V6 — Module Coaching : coachs et entreprises
-- ═══════════════════════════════════════════════════════════════════

CREATE SCHEMA IF NOT EXISTS coaching;

-- ── coaching.coaches ──────────────────────────────────────────────────────────

CREATE TABLE coaching.coaches (
    id          VARCHAR(36)     PRIMARY KEY,
    user_id     VARCHAR(36)     NOT NULL UNIQUE REFERENCES users.users (id),
    bio         VARCHAR(1000),
    hourly_rate NUMERIC(10,2),
    currency    VARCHAR(3)      NOT NULL DEFAULT 'MAD',
    is_available BOOLEAN        NOT NULL DEFAULT TRUE,
    avg_rating  NUMERIC(3,2)    NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coaches_user       ON coaching.coaches (user_id);
CREATE INDEX idx_coaches_available  ON coaching.coaches (is_available);
CREATE INDEX idx_coaches_rating     ON coaching.coaches (avg_rating DESC);

-- ── coaching.coach_sports ─────────────────────────────────────────────────────

CREATE TABLE coaching.coach_sports (
    coach_id    VARCHAR(36)     NOT NULL REFERENCES coaching.coaches (id) ON DELETE CASCADE,
    sport       VARCHAR(100)    NOT NULL,
    proficiency VARCHAR(30)     NOT NULL,
    PRIMARY KEY (coach_id, sport)
);

-- ── coaching.coaching_offers ──────────────────────────────────────────────────

CREATE TABLE coaching.coaching_offers (
    id              VARCHAR(36)     PRIMARY KEY,
    coach_id        VARCHAR(36)     NOT NULL REFERENCES coaching.coaches (id) ON DELETE CASCADE,
    title           VARCHAR(200)    NOT NULL,
    description     VARCHAR(1000),
    target_audience VARCHAR(30)     NOT NULL DEFAULT 'INDIVIDUAL',
    sport           VARCHAR(100)    NOT NULL,
    duration_min    INT             NOT NULL,
    price           NUMERIC(10,2)   NOT NULL,
    currency        VARCHAR(3)      NOT NULL DEFAULT 'MAD',
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_offers_coach   ON coaching.coaching_offers (coach_id);
CREATE INDEX idx_offers_sport   ON coaching.coaching_offers (sport);

-- ── coaching.bookings ─────────────────────────────────────────────────────────

CREATE TABLE coaching.bookings (
    id              VARCHAR(36)     PRIMARY KEY,
    offer_id        VARCHAR(36)     NOT NULL REFERENCES coaching.coaching_offers (id),
    client_id       VARCHAR(36)     NOT NULL,
    client_type     VARCHAR(30)     NOT NULL DEFAULT 'USER',
    scheduled_at    TIMESTAMPTZ     NOT NULL,
    status          VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bookings_offer     ON coaching.bookings (offer_id);
CREATE INDEX idx_bookings_client    ON coaching.bookings (client_id);
CREATE INDEX idx_bookings_status    ON coaching.bookings (status);

-- ── coaching.enterprise_profiles ─────────────────────────────────────────────

CREATE TABLE coaching.enterprise_profiles (
    id              VARCHAR(36)     PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    contact_email   VARCHAR(255)    NOT NULL UNIQUE,
    budget          NUMERIC(12,2),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE coaching.enterprise_sports (
    enterprise_id   VARCHAR(36)     NOT NULL REFERENCES coaching.enterprise_profiles (id) ON DELETE CASCADE,
    sport           VARCHAR(100)    NOT NULL,
    PRIMARY KEY (enterprise_id, sport)
);
