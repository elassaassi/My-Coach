package org.elas.momentum.rating.domain;

import org.elas.momentum.rating.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PlayerRatingTest {

    @Test
    @DisplayName("PlayerRating.create génère un ID et valide les scores")
    void create_shouldGenerateId() {
        var rating = PlayerRating.create("act1", "user1", "user2", 5, 4, 3, PlayerLevel.PRO, true, "Great!");
        assertThat(rating.getId()).isNotNull();
        assertThat(rating.getId().value()).isNotBlank();
        assertThat(rating.getBehavior()).isEqualTo(5);
        assertThat(rating.getTechnicality()).isEqualTo(4);
        assertThat(rating.isManOfMatch()).isTrue();
    }

    @Test
    @DisplayName("Score invalide lève IllegalArgumentException")
    void create_invalidScore_shouldThrow() {
        assertThatThrownBy(() -> PlayerRating.create("act1", "user1", "user2", 0, 4, 3, PlayerLevel.AMATEUR, false, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PlayerRating.create("act1", "user1", "user2", 5, 6, 3, PlayerLevel.AMATEUR, false, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("PlayerStats.empty crée des stats vides")
    void stats_empty_shouldHaveZeroValues() {
        var stats = PlayerStats.empty("user1", "football");
        assertThat(stats.totalRatings()).isZero();
        assertThat(stats.proScore()).isZero();
        assertThat(stats.userId()).isEqualTo("user1");
        assertThat(stats.sport()).isEqualTo("football");
    }

    @Test
    @DisplayName("PlayerStats.withUpdatedRating calcule proScore")
    void stats_withUpdatedRating_shouldCalculateScore() {
        var stats = PlayerStats.empty("user1", "all");
        var updated = stats.withUpdatedRating(5.0, 5.0, 5.0, 1, 1);
        assertThat(updated.totalRatings()).isEqualTo(1);
        assertThat(updated.proScore()).isGreaterThan(0);
        assertThat(updated.proScore()).isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("LeaderboardEntry est un value object immuable")
    void leaderboardEntry_isValueObject() {
        var entry = new LeaderboardEntry("football", 1, "user1", 95);
        assertThat(entry.sport()).isEqualTo("football");
        assertThat(entry.rank()).isEqualTo(1);
        assertThat(entry.score()).isEqualTo(95);
    }

    @Test
    @DisplayName("RatingId.generate produit des IDs uniques")
    void ratingId_generate_shouldBeUnique() {
        var id1 = RatingId.generate();
        var id2 = RatingId.generate();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("PlayerRating.create requiert activityId non null")
    void create_nullActivityId_shouldThrow() {
        assertThatThrownBy(() -> PlayerRating.create(null, "user1", "user2", 4, 4, 4, PlayerLevel.AMATEUR, false, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("ProScore parfait = 5,5,5 avec man of match = 100")
    void proScore_perfect() {
        var stats = PlayerStats.empty("u", "all");
        var updated = stats.withUpdatedRating(5.0, 5.0, 5.0, 10, 6);
        assertThat(updated.proScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("PlayerLevel enum contient AMATEUR, SEMI_PRO, PRO, GOAT")
    void playerLevel_enum() {
        assertThat(PlayerLevel.values()).containsExactly(
                PlayerLevel.AMATEUR, PlayerLevel.SEMI_PRO, PlayerLevel.PRO, PlayerLevel.GOAT);
    }

    @Test
    @DisplayName("PlayerRating.create sans man of match")
    void create_noManOfMatch() {
        var rating = PlayerRating.create("act1", "user1", "user2", 3, 3, 3, PlayerLevel.SEMI_PRO, false, "OK");
        assertThat(rating.isManOfMatch()).isFalse();
        assertThat(rating.getLevel()).isEqualTo(PlayerLevel.SEMI_PRO);
    }

    @Test
    @DisplayName("Reconstitution préserve toutes les données")
    void reconstitute_preservesData() {
        var id = RatingId.generate();
        var now = java.time.Instant.now();
        var rating = PlayerRating.reconstitute(id, "act1", "r1", "r2", 5, 5, 5, PlayerLevel.GOAT, true, "Excellent", now);
        assertThat(rating.getId()).isEqualTo(id);
        assertThat(rating.getCreatedAt()).isEqualTo(now);
    }
}
