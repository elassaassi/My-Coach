package org.elas.momentum.activity.domain;

import org.elas.momentum.activity.domain.exception.ActivityFullException;
import org.elas.momentum.activity.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires domaine — <50ms, aucune dépendance externe.
 */
class ActivityTest {

    private Activity createActivity(int maxParticipants) {
        var location = new Location(33.59, -7.61, "Complexe sportif", "Casablanca", "MA");
        return Activity.create(
                "organizer-1",
                "Match de foot",
                "football",
                "INTERMEDIATE",
                location,
                Instant.now().plusSeconds(3600),
                maxParticipants
        );
    }

    @Test
    @DisplayName("Création d'activité — organisateur automatiquement inscrit")
    void create_shouldAutoJoinOrganizer() {
        var activity = createActivity(5);

        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.OPEN);
        assertThat(activity.getCurrentParticipantsCount()).isEqualTo(1);
        assertThat(activity.isParticipant("organizer-1")).isTrue();
    }

    @Test
    @DisplayName("Join — nouvel utilisateur rejoint l'activité")
    void join_shouldAddParticipant() {
        var activity = createActivity(5);
        activity.join("user-2");

        assertThat(activity.getCurrentParticipantsCount()).isEqualTo(2);
        assertThat(activity.isParticipant("user-2")).isTrue();
    }

    @Test
    @DisplayName("Join — activité complète passe au statut FULL")
    void join_shouldMarkFullWhenCapacityReached() {
        var activity = createActivity(2); // organizer + 1 other
        activity.join("user-2");

        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.FULL);
        assertThat(activity.isFull()).isTrue();
    }

    @Test
    @DisplayName("Join activité FULL lève ActivityFullException")
    void join_whenFull_shouldThrow() {
        var activity = createActivity(2);
        activity.join("user-2");

        assertThatThrownBy(() -> activity.join("user-3"))
                .isInstanceOf(ActivityFullException.class);
    }

    @Test
    @DisplayName("Join en doublon lève IllegalStateException")
    void join_duplicate_shouldThrow() {
        var activity = createActivity(5);
        assertThatThrownBy(() -> activity.join("organizer-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already a participant");
    }

    @Test
    @DisplayName("Leave — participant quitte l'activité, statut revient à OPEN si FULL")
    void leave_shouldRemoveParticipantAndReopenIfFull() {
        var activity = createActivity(2);
        activity.join("user-2"); // → FULL
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.FULL);

        activity.leave("user-2");
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.OPEN);
        assertThat(activity.getCurrentParticipantsCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("L'organisateur ne peut pas quitter son activité")
    void leave_asOrganizer_shouldThrow() {
        var activity = createActivity(5);
        assertThatThrownBy(() -> activity.leave("organizer-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Organizer");
    }

    @Test
    @DisplayName("Annulation — statut passe à CANCELLED")
    void cancel_shouldSetStatusCancelled() {
        var activity = createActivity(5);
        activity.cancel();
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.CANCELLED);
    }

    @Test
    @DisplayName("Activité avec moins de 2 participants lève une exception")
    void create_withOnlyOneParticipant_shouldThrow() {
        var location = new Location(33.59, -7.61, "", "Casablanca", "MA");
        assertThatThrownBy(() -> Activity.create(
                "org", "title", "sport", "BEGINNER", location,
                Instant.now().plusSeconds(3600), 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
