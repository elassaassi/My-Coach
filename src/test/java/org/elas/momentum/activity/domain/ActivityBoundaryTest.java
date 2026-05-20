package org.elas.momentum.activity.domain;

import org.elas.momentum.activity.domain.exception.ActivityFullException;
import org.elas.momentum.activity.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class ActivityBoundaryTest {

    private Activity create(int max) {
        return Activity.create("org-1", "Match", "football", "INTERMEDIATE",
                new Location(33.59, -7.61, "Stade", "Casablanca", "MA"),
                Instant.now().plusSeconds(3600), max);
    }

    // ── cancel() guard ────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel() depuis COMPLETED lève IllegalStateException")
    void cancel_fromCompleted_throws() {
        var act = create(5);
        act.complete();
        assertThatThrownBy(act::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("cancel() depuis CANCELLED lève IllegalStateException")
    void cancel_fromCancelled_throws() {
        var act = create(5);
        act.cancel();
        assertThatThrownBy(act::cancel).isInstanceOf(IllegalStateException.class);
    }

    // ── complete() guard ──────────────────────────────────────────────────────

    @Test
    @DisplayName("complete() depuis CANCELLED lève IllegalStateException")
    void complete_fromCancelled_throws() {
        var act = create(5);
        act.cancel();
        assertThatThrownBy(act::complete).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("complete() depuis ONGOING fonctionne")
    void complete_fromOngoing_succeeds() {
        var act = create(5);
        act.startOngoing();
        act.complete();
        assertThat(act.getStatus()).isEqualTo(ActivityStatus.COMPLETED);
    }

    // ── join() guard et boundary ──────────────────────────────────────────────

    @Test
    @DisplayName("join() depuis ONGOING lève IllegalStateException")
    void join_fromOngoing_throws() {
        var act = create(5);
        act.startOngoing();
        assertThatThrownBy(() -> act.join("user-2")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("join() depuis COMPLETED lève ActivityFullException (status != OPEN)")
    void join_fromCompleted_throws() {
        var act = create(5);
        act.complete();
        assertThatThrownBy(() -> act.join("user-2")).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("join() exactement au maximum de participants lève ActivityFullException")
    void join_atExactCapacity_throws() {
        var act = create(3); // org + 2
        act.join("user-2");
        act.join("user-3"); // maintenant FULL
        assertThat(act.isFull()).isTrue();
        assertThatThrownBy(() -> act.join("user-4")).isInstanceOf(ActivityFullException.class);
    }

    // ── isFull() boundary ─────────────────────────────────────────────────────

    @Test
    @DisplayName("isFull() retourne false quand une place reste")
    void isFull_oneSlotLeft_isFalse() {
        var act = create(3); // org inscrit = 1 participant
        act.join("user-2"); // 2 sur 3
        assertThat(act.isFull()).isFalse();
    }

    @Test
    @DisplayName("isFull() retourne true quand la capacité est exactement atteinte")
    void isFull_exactCapacity_isTrue() {
        var act = create(2);
        act.join("user-2");
        assertThat(act.isFull()).isTrue();
        assertThat(act.getStatus()).isEqualTo(ActivityStatus.FULL);
    }

    // ── ActivityId ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ActivityId.of() restitue la valeur exacte")
    void activityId_of_restoresValue() {
        var id = ActivityId.of("act-42");
        assertThat(id.value()).isEqualTo("act-42");
    }

    // ── reconstitute() ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Activity.reconstitute() restaure l'état complet depuis la persistance")
    void reconstitute_preservesState() {
        var id = ActivityId.generate();
        var location = new Location(33.0, -7.0, "terrain", "Rabat", "MA");
        var now = Instant.now();
        var participants = java.util.List.of(Participant.of("org-1"));
        var act = Activity.reconstitute(id, "org-1", "Tennis", "desc",
                "tennis", "ADVANCED", location, now, 4,
                ActivityStatus.OPEN, participants, now, now);
        assertThat(act.getId()).isEqualTo(id);
        assertThat(act.getDescription()).isEqualTo("desc");
        assertThat(act.getStatus()).isEqualTo(ActivityStatus.OPEN);
    }
}