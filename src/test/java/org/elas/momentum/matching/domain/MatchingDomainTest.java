package org.elas.momentum.matching.domain;

import org.elas.momentum.matching.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MatchingDomainTest {

    private static MatchCriteria criteria() {
        return new MatchCriteria("football", "INTERMEDIATE", 33.59, -7.61, 10);
    }

    // ── MatchRequestId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("MatchRequestId.generate() produit des IDs uniques non nuls")
    void matchRequestId_generate_isUnique() {
        var a = MatchRequestId.generate();
        var b = MatchRequestId.generate();
        assertThat(a).isNotNull();
        assertThat(a.value()).isNotBlank();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("MatchRequestId.of() restitue la valeur")
    void matchRequestId_of_restoresValue() {
        var id = MatchRequestId.of("req-99");
        assertThat(id.value()).isEqualTo("req-99");
    }

    // ── MatchRequest ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("MatchRequest.create() — statut initial PENDING")
    void matchRequest_create_isPending() {
        var req = MatchRequest.create("user-1", criteria());
        assertThat(req.getId()).isNotNull();
        assertThat(req.getRequesterId()).isEqualTo("user-1");
        assertThat(req.getStatus()).isEqualTo(MatchStatus.PENDING);
        assertThat(req.getCreatedAt()).isNotNull();
        assertThat(req.getMatchedUserId()).isNull();
        assertThat(req.getMatchScore()).isZero();
    }

    @Test
    @DisplayName("MatchRequest.cancel() depuis PENDING passe au statut CANCELLED")
    void matchRequest_cancel_fromPending() {
        var req = MatchRequest.create("user-1", criteria());
        req.cancel();
        assertThat(req.getStatus()).isEqualTo(MatchStatus.CANCELLED);
        assertThat(req.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("MatchRequest.cancel() depuis FOUND lève IllegalStateException")
    void matchRequest_cancel_fromFound_throws() {
        var req = MatchRequest.create("user-1", criteria());
        req.matchFound("user-2", 85.0);
        assertThatThrownBy(req::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pending");
    }

    @Test
    @DisplayName("MatchRequest.cancel() depuis NO_MATCH lève IllegalStateException")
    void matchRequest_cancel_fromNoMatch_throws() {
        var req = MatchRequest.create("user-1", criteria());
        req.noMatchFound();
        assertThatThrownBy(req::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("MatchRequest.matchFound() enregistre l'utilisateur matché et le score")
    void matchRequest_matchFound() {
        var req = MatchRequest.create("user-1", criteria());
        req.matchFound("user-2", 92.5);
        assertThat(req.getStatus()).isEqualTo(MatchStatus.FOUND);
        assertThat(req.getMatchedUserId()).isEqualTo("user-2");
        assertThat(req.getMatchScore()).isEqualTo(92.5);
    }

    @Test
    @DisplayName("MatchRequest.noMatchFound() passe au statut NO_MATCH")
    void matchRequest_noMatchFound() {
        var req = MatchRequest.create("user-1", criteria());
        req.noMatchFound();
        assertThat(req.getStatus()).isEqualTo(MatchStatus.NO_MATCH);
    }

    @Test
    @DisplayName("MatchRequest getCriteria() retourne les critères fournis")
    void matchRequest_getCriteria() {
        var c = criteria();
        var req = MatchRequest.create("user-1", c);
        assertThat(req.getCriteria()).isEqualTo(c);
    }

    // ── MatchOutcome ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("MatchOutcome.describe() Found affiche userId, score et distance")
    void matchOutcome_describe_found() {
        var outcome = new MatchOutcome.Found("user-2", 87.5, 3.2);
        var desc = MatchOutcome.describe(outcome);
        assertThat(desc).contains("user-2").contains("87").contains("3");
    }

    @Test
    @DisplayName("MatchOutcome.describe() NoMatch affiche la raison")
    void matchOutcome_describe_noMatch() {
        var outcome = new MatchOutcome.NoMatch("Aucun joueur disponible");
        var desc = MatchOutcome.describe(outcome);
        assertThat(desc).contains("Aucun joueur disponible");
    }

    @Test
    @DisplayName("MatchOutcome.describe() Pending affiche l'ID de la requête")
    void matchOutcome_describe_pending() {
        var outcome = new MatchOutcome.Pending("req-123");
        var desc = MatchOutcome.describe(outcome);
        assertThat(desc).contains("req-123");
    }

    @Test
    @DisplayName("MatchOutcome records sont accessibles (record components)")
    void matchOutcome_recordComponents() {
        var found = new MatchOutcome.Found("uid", 80.0, 5.0);
        assertThat(found.matchedUserId()).isEqualTo("uid");
        assertThat(found.score()).isEqualTo(80.0);
        assertThat(found.distanceKm()).isEqualTo(5.0);

        var noMatch = new MatchOutcome.NoMatch("raison");
        assertThat(noMatch.reason()).isEqualTo("raison");

        var pending = new MatchOutcome.Pending("id-req");
        assertThat(pending.matchRequestId()).isEqualTo("id-req");
    }
}