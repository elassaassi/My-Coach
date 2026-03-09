-- ═══════════════════════════════════════════════════════════════════
--  V9 — Coaching module adjustments: certifications table,
--       relax coach_sports proficiency constraint
-- ═══════════════════════════════════════════════════════════════════

-- Allow proficiency to be nullable so JPA ElementCollection on sports
-- (List<String>) can map correctly to the coach_sports table
ALTER TABLE coaching.coach_sports
    ALTER COLUMN proficiency DROP NOT NULL,
    ALTER COLUMN proficiency SET DEFAULT NULL;

-- ── coaching.coach_certifications ─────────────────────────────────────────────

CREATE TABLE coaching.coach_certifications (
    coach_id        VARCHAR(36)     NOT NULL REFERENCES coaching.coaches (id) ON DELETE CASCADE,
    certification   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (coach_id, certification)
);

CREATE INDEX idx_coach_certifications ON coaching.coach_certifications (coach_id);
