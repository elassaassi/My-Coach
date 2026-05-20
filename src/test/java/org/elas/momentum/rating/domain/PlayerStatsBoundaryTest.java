package org.elas.momentum.rating.domain;

import org.elas.momentum.rating.domain.model.PlayerLevel;
import org.elas.momentum.rating.domain.model.PlayerStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class PlayerStatsBoundaryTest {

    // ── calculateProScore boundary mutations ──────────────────────────────────

    @Test
    @DisplayName("proScore avec total=0 retourne 0 (pas de division par zéro)")
    void proScore_totalZero_returnsZero() {
        var stats = PlayerStats.empty("u", "football");
        assertThat(stats.proScore()).isZero();
    }

    @Test
    @DisplayName("proScore avec notes minimum (1,1,1) et aucun MoM")
    void proScore_minRatings() {
        var stats = PlayerStats.empty("u", "all");
        // behavior=1, tech=1, teamwork=1, total=1, mom=0
        // base = (1*0.3 + 1*0.5 + 1*0.2) / 5 * 70 = 1/5*70 = 14
        var updated = stats.withUpdatedRating(1.0, 1.0, 1.0, 1, 0);
        assertThat(updated.proScore()).isEqualTo(14);
    }

    @Test
    @DisplayName("proScore avec notes maximum (5,5,5) et 1 MoM = 75")
    void proScore_maxRatings_oneMom() {
        var stats = PlayerStats.empty("u", "all");
        // base = (5*0.3 + 5*0.5 + 5*0.2) / 5 * 70 = 70, bonus = min(1*5, 30) = 5
        var updated = stats.withUpdatedRating(5.0, 5.0, 5.0, 1, 1);
        assertThat(updated.proScore()).isEqualTo(75);
    }

    @Test
    @DisplayName("proScore bonus MoM plafonné à 30 — 6 MoM ne dépasse pas le plafond")
    void proScore_momBonusCapped() {
        var stats = PlayerStats.empty("u", "all");
        // 6 MoM = min(30, 30) = 30, base=70 → proScore=100
        var updated = stats.withUpdatedRating(5.0, 5.0, 5.0, 6, 6);
        assertThat(updated.proScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("proScore plafonné à 100 même avec bonus excessif")
    void proScore_cappedAt100() {
        var stats = PlayerStats.empty("u", "all");
        var updated = stats.withUpdatedRating(5.0, 5.0, 5.0, 100, 100);
        assertThat(updated.proScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("proScore pondère la technicité plus que le comportement")
    void proScore_techHeavierWeight() {
        var stats = PlayerStats.empty("u", "all");
        // tech=5, behavior=1, teamwork=1 vs tech=1, behavior=5, teamwork=1
        var highTech = stats.withUpdatedRating(1.0, 5.0, 1.0, 1, 0);
        var highBehavior = stats.withUpdatedRating(5.0, 1.0, 1.0, 1, 0);
        assertThat(highTech.proScore()).isGreaterThan(highBehavior.proScore());
    }

    // ── levelFromScore boundary mutations ─────────────────────────────────────

    @ParameterizedTest(name = "score={0} → {1}")
    @CsvSource({
        "0,   AMATEUR",
        "39,  AMATEUR",
        "40,  SEMI_PRO",
        "59,  SEMI_PRO",
        "60,  PRO",
        "79,  PRO",
        "80,  GOAT",
        "100, GOAT"
    })
    @DisplayName("levelFromScore() respecte les seuils exacts")
    void levelFromScore_exactBoundaries(int score, String expectedLevel) {
        var level = PlayerStats.levelFromScore(score);
        assertThat(level).isEqualTo(PlayerLevel.valueOf(expectedLevel));
    }

    @Test
    @DisplayName("withUpdatedRating dérive correctement le PlayerLevel")
    void withUpdatedRating_derivesLevel() {
        var stats = PlayerStats.empty("u", "all");
        // score 0 → AMATEUR
        assertThat(stats.level()).isEqualTo(PlayerLevel.AMATEUR);

        // min ratings → score 14 → AMATEUR
        var l1 = stats.withUpdatedRating(1.0, 1.0, 1.0, 1, 0);
        assertThat(l1.level()).isEqualTo(PlayerLevel.AMATEUR);

        // score 75 → PRO
        var l2 = stats.withUpdatedRating(5.0, 5.0, 5.0, 1, 1);
        assertThat(l2.level()).isEqualTo(PlayerLevel.PRO);

        // score 100 → GOAT
        var l3 = stats.withUpdatedRating(5.0, 5.0, 5.0, 6, 6);
        assertThat(l3.level()).isEqualTo(PlayerLevel.GOAT);
    }
}