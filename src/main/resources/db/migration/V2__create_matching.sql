-- ═══════════════════════════════════════════════════════════════════
--  V2 — Tables du module Matching
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE matching.match_requests (
    id               VARCHAR(36)      PRIMARY KEY,
    requester_id     VARCHAR(36)      NOT NULL,
    sport            VARCHAR(100)     NOT NULL,
    proficiency      VARCHAR(30)      NOT NULL,
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    max_distance_km  INT              NOT NULL DEFAULT 20,
    status           VARCHAR(20)      NOT NULL DEFAULT 'PENDING',
    matched_user_id  VARCHAR(36),
    match_score      DOUBLE PRECISION NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_match_requests_requester ON matching.match_requests (requester_id);
CREATE INDEX idx_match_requests_status    ON matching.match_requests (status);
CREATE INDEX idx_match_requests_sport     ON matching.match_requests (sport);
