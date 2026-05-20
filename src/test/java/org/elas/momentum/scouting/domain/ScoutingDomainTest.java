package org.elas.momentum.scouting.domain;

import org.elas.momentum.scouting.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ScoutingDomainTest {

    // ── ScoutingInterest ──────────────────────────────────────────────────────

    @Test
    @DisplayName("ScoutingInterest.accept() sur PENDING passe à ACCEPTED")
    void interest_accept_fromPending() {
        var interest = ScoutingInterest.create("rec-1", "talent-1", "Intéressant");
        interest.accept();
        assertThat(interest.getStatus()).isEqualTo(InterestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("ScoutingInterest.accept() sur REJECTED lève IllegalStateException")
    void interest_accept_fromRejected_throws() {
        var interest = ScoutingInterest.create("rec-1", "talent-1", null);
        interest.reject();
        assertThatThrownBy(interest::accept).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("ScoutingInterest.reject() sur PENDING passe à REJECTED")
    void interest_reject_fromPending() {
        var interest = ScoutingInterest.create("rec-1", "talent-1", "Pas convaincu");
        interest.reject();
        assertThat(interest.getStatus()).isEqualTo(InterestStatus.REJECTED);
    }

    @Test
    @DisplayName("ScoutingInterest.reject() sur ACCEPTED lève IllegalStateException")
    void interest_reject_fromAccepted_throws() {
        var interest = ScoutingInterest.create("rec-1", "talent-1", null);
        interest.accept();
        assertThatThrownBy(interest::reject).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("ScoutingInterest.reconstitute() préserve tous les champs")
    void interest_reconstitute() {
        var now = Instant.now();
        var interest = ScoutingInterest.reconstitute("id-1", "rec-1", "talent-1",
                InterestStatus.ACCEPTED, "Note", now);
        assertThat(interest.getId().value()).isEqualTo("id-1");
        assertThat(interest.getStatus()).isEqualTo(InterestStatus.ACCEPTED);
        assertThat(interest.getNote()).isEqualTo("Note");
        assertThat(interest.getCreatedAt()).isEqualTo(now);
    }

    // ── RecruiterProfile ──────────────────────────────────────────────────────

    @Test
    @DisplayName("RecruiterProfile.create() nominal — organisation non vide")
    void recruiter_create_nominal() {
        var profile = RecruiterProfile.create("user-1", "Club Élite", List.of("football"), "PRO");
        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getUserId()).isEqualTo("user-1");
        assertThat(profile.getOrganization()).isEqualTo("Club Élite");
        assertThat(profile.getTargetSports()).containsExactly("football");
        assertThat(profile.getTargetLevel()).isEqualTo("PRO");
    }

    @Test
    @DisplayName("RecruiterProfile.create() organisation vide lève IllegalArgumentException")
    void recruiter_create_blankOrg_throws() {
        assertThatThrownBy(() -> RecruiterProfile.create("user-1", "", List.of(), "PRO"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RecruiterProfile.create("user-1", "  ", List.of(), "PRO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("RecruiterProfile.create() organisation null lève NullPointerException")
    void recruiter_create_nullOrg_throws() {
        assertThatThrownBy(() -> RecruiterProfile.create("user-1", null, List.of(), "PRO"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("RecruiterProfile.create() userId null lève NullPointerException")
    void recruiter_create_nullUserId_throws() {
        assertThatThrownBy(() -> RecruiterProfile.create(null, "Org", List.of(), "PRO"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("RecruiterProfile.create() avec targetSports null utilise une liste vide")
    void recruiter_create_nullSports_usesEmptyList() {
        var profile = RecruiterProfile.create("user-1", "Org", null, "AMATEUR");
        assertThat(profile.getTargetSports()).isEmpty();
    }

    @Test
    @DisplayName("RecruiterProfile.reconstitute() préserve tous les champs")
    void recruiter_reconstitute() {
        var now = Instant.now();
        var profile = RecruiterProfile.reconstitute("id-rec", "user-2", "Org",
                List.of("tennis"), "ELITE", now);
        assertThat(profile.getId().value()).isEqualTo("id-rec");
        assertThat(profile.getOrganization()).isEqualTo("Org");
        assertThat(profile.getCreatedAt()).isEqualTo(now);
    }

    // ── InterestId ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("InterestId.of() restitue la valeur")
    void interestId_of() {
        var id = InterestId.of("int-99");
        assertThat(id.value()).isEqualTo("int-99");
    }

    // ── RecruiterId ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("RecruiterId.of() restitue la valeur")
    void recruiterId_of() {
        var id = RecruiterId.of("rec-99");
        assertThat(id.value()).isEqualTo("rec-99");
    }
}