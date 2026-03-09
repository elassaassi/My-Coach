package org.elas.momentum.highlight.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "highlight_of_day", schema = "highlight")
class HighlightOfDayEntity {

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "highlight_id", nullable = false)
    private String highlightId;

    @Column(name = "selected_at", nullable = false)
    private Instant selectedAt;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getHighlightId() { return highlightId; }
    public void setHighlightId(String highlightId) { this.highlightId = highlightId; }
    public Instant getSelectedAt() { return selectedAt; }
    public void setSelectedAt(Instant selectedAt) { this.selectedAt = selectedAt; }
}
