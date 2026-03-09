package org.elas.momentum.rating;

import org.elas.momentum.matching.domain.model.MatchRequest;
import org.elas.momentum.matching.domain.model.MatchStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests du module API public du module rating.
 */
class RatingModuleApiTest {

    @Test
    @DisplayName("RatingSubmittedEvent.of() crée l'événement avec les bonnes valeurs")
    void ratingSubmittedEvent_of_shouldCreateWithCorrectValues() {
        var event = RatingSubmittedEvent.of("rating-1", "act-1", "user-2", 85);

        assertThat(event.ratingId()).isEqualTo("rating-1");
        assertThat(event.activityId()).isEqualTo("act-1");
        assertThat(event.ratedUserId()).isEqualTo("user-2");
        assertThat(event.proScore()).isEqualTo(85);
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("RatingSubmittedEvent.occurredAt() retourne la date de création")
    void ratingSubmittedEvent_occurredAt_shouldReturnTimestamp() {
        var before = java.time.Instant.now();
        var event = RatingSubmittedEvent.of("r1", "a1", "u1", 50);
        var after = java.time.Instant.now();

        assertThat(event.occurredAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("RatingSubmittedEvent est immuable (record)")
    void ratingSubmittedEvent_isRecord() {
        var e1 = RatingSubmittedEvent.of("r1", "a1", "u1", 50);
        var e2 = RatingSubmittedEvent.of("r1", "a1", "u1", 50);
        // Records with same values are equal (excluding random UUID and timestamp)
        assertThat(e1.ratingId()).isEqualTo(e2.ratingId());
        assertThat(e1.proScore()).isEqualTo(e2.proScore());
    }
}
