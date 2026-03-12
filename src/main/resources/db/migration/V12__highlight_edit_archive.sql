ALTER TABLE highlight.highlights
    ADD COLUMN archived_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN edited_at   TIMESTAMP WITH TIME ZONE;

CREATE INDEX idx_highlights_archived ON highlight.highlights (archived_at) WHERE archived_at IS NOT NULL;