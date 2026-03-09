package org.elas.momentum.activity.domain;

import org.elas.momentum.activity.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests complémentaires pour les nouvelles méthodes du domaine Activity.
 */
class ActivityDomainExtraTest {

    private Activity createActivity(int maxParticipants) {
        return Activity.create("organizer-1", "Match de foot", "football", "INTERMEDIATE",
                new Location(33.59, -7.61, "Complexe sportif", "Casablanca", "MA"),
                Instant.now().plusSeconds(3600), maxParticipants);
    }

    @Test
    @DisplayName("complete() passe le statut à COMPLETED")
    void complete_shouldSetStatusCompleted() {
        var activity = createActivity(5);
        activity.complete();
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.COMPLETED);
    }

    @Test
    @DisplayName("complete() depuis FULL fonctionne")
    void complete_fromFull_shouldWork() {
        var activity = createActivity(2);
        activity.join("user-2");
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.FULL);
        activity.complete();
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.COMPLETED);
    }

    @Test
    @DisplayName("complete() sur une activité déjà COMPLETED lève une exception")
    void complete_alreadyCompleted_shouldThrow() {
        var activity = createActivity(5);
        activity.complete();
        assertThatThrownBy(activity::complete).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("startOngoing() passe le statut à ONGOING")
    void startOngoing_shouldSetStatusOngoing() {
        var activity = createActivity(5);
        activity.startOngoing();
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.ONGOING);
    }

    @Test
    @DisplayName("startOngoing() depuis FULL fonctionne")
    void startOngoing_fromFull_shouldWork() {
        var activity = createActivity(2);
        activity.join("user-2");
        activity.startOngoing();
        assertThat(activity.getStatus()).isEqualTo(ActivityStatus.ONGOING);
    }

    @Test
    @DisplayName("startOngoing() depuis CANCELLED lève une exception")
    void startOngoing_fromCancelled_shouldThrow() {
        var activity = createActivity(5);
        activity.cancel();
        assertThatThrownBy(activity::startOngoing).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("ActivityId.generate() produit des IDs uniques")
    void activityId_generate_shouldBeUnique() {
        var id1 = ActivityId.generate();
        var id2 = ActivityId.generate();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.value()).isNotBlank();
    }

    @Test
    @DisplayName("Location stocke les coordonnées et la ville")
    void location_shouldStoreData() {
        var location = new Location(33.9, -6.8, "Rabat", "Rabat-Salé", "MA");
        assertThat(location.latitude()).isEqualTo(33.9);
        assertThat(location.longitude()).isEqualTo(-6.8);
        assertThat(location.city()).isEqualTo("Rabat-Salé");
        assertThat(location.country()).isEqualTo("MA");
    }
}
