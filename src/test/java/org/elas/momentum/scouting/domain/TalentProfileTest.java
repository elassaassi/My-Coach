package org.elas.momentum.scouting.domain;

import org.elas.momentum.scouting.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Scouting domain models — no Spring context, no external deps.
 */
class TalentProfileTest {

    @Test
    @DisplayName("TalentProfile.create validates proScore must be between 0 and 100")
    void create_invalidProScore_shouldThrow() {
        assertThatThrownBy(() -> TalentProfile.create("user-1", "football", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proScore");

        assertThatThrownBy(() -> TalentProfile.create("user-1", "football", 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proScore");
    }

    @Test
    @DisplayName("TalentProfile.create with valid proScore succeeds")
    void create_validProScore_shouldSucceed() {
        var profile = TalentProfile.create("user-1", "football", 75);

        assertThat(profile.getUserId().value()).isEqualTo("user-1");
        assertThat(profile.getSport()).isEqualTo("football");
        assertThat(profile.getProScore()).isEqualTo(75);
        assertThat(profile.isVisible()).isTrue();
        assertThat(profile.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.OPEN);
    }

    @Test
    @DisplayName("updateScore validates range 0-100")
    void updateScore_outOfRange_shouldThrow() {
        var profile = TalentProfile.create("user-1", "tennis", 50);

        assertThatThrownBy(() -> profile.updateScore(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proScore");

        assertThatThrownBy(() -> profile.updateScore(105))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proScore");
    }

    @Test
    @DisplayName("updateScore with valid value updates the score")
    void updateScore_valid_shouldUpdate() {
        var profile = TalentProfile.create("user-1", "basketball", 60);
        profile.updateScore(85);
        assertThat(profile.getProScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("setVisible changes the visibility flag")
    void setVisible_shouldChangeVisibility() {
        var profile = TalentProfile.create("user-1", "swimming", 70);
        assertThat(profile.isVisible()).isTrue();

        profile.setVisible(false);
        assertThat(profile.isVisible()).isFalse();

        profile.setVisible(true);
        assertThat(profile.isVisible()).isTrue();
    }

    @Test
    @DisplayName("ScoutingInterest.create sets PENDING status")
    void scoutingInterest_create_shouldSetPendingStatus() {
        var interest = ScoutingInterest.create("recruiter-1", "talent-42", "Impressive performance");

        assertThat(interest.getStatus()).isEqualTo(InterestStatus.PENDING);
        assertThat(interest.getRecruiterId()).isEqualTo("recruiter-1");
        assertThat(interest.getTalentId()).isEqualTo("talent-42");
        assertThat(interest.getNote()).isEqualTo("Impressive performance");
        assertThat(interest.getId()).isNotNull();
        assertThat(interest.getId().value()).isNotBlank();
    }

    @Test
    @DisplayName("accept() transitions ScoutingInterest to ACCEPTED")
    void accept_shouldTransitionToAccepted() {
        var interest = ScoutingInterest.create("recruiter-1", "talent-1", null);
        assertThat(interest.getStatus()).isEqualTo(InterestStatus.PENDING);

        interest.accept();
        assertThat(interest.getStatus()).isEqualTo(InterestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("reject() transitions ScoutingInterest to REJECTED")
    void reject_shouldTransitionToRejected() {
        var interest = ScoutingInterest.create("recruiter-1", "talent-1", "Great player");

        interest.reject();
        assertThat(interest.getStatus()).isEqualTo(InterestStatus.REJECTED);
    }

    @Test
    @DisplayName("TalentId.generate produces unique IDs")
    void talentId_generate_shouldProduceUniqueIds() {
        var id1 = TalentId.generate();
        var id2 = TalentId.generate();
        var id3 = TalentId.generate();

        assertThat(id1).isNotEqualTo(id2);
        assertThat(id2).isNotEqualTo(id3);
        assertThat(id1.value()).isNotBlank();
    }

    @Test
    @DisplayName("ScoutingInterest.create with null recruiterId throws NullPointerException")
    void scoutingInterest_nullRecruiterId_shouldThrow() {
        assertThatThrownBy(() -> ScoutingInterest.create(null, "talent-1", "note"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("RecruiterProfile.create sets all fields correctly")
    void recruiterProfile_create_shouldSetAllFields() {
        var recruiter = RecruiterProfile.create(
                "user-99", "Club Atlas Casablanca",
                java.util.List.of("football", "athletics"), "SEMI_PRO");

        assertThat(recruiter.getUserId()).isEqualTo("user-99");
        assertThat(recruiter.getOrganization()).isEqualTo("Club Atlas Casablanca");
        assertThat(recruiter.getTargetSports()).containsExactlyInAnyOrder("football", "athletics");
        assertThat(recruiter.getTargetLevel()).isEqualTo("SEMI_PRO");
        assertThat(recruiter.getId()).isNotNull();
    }
}
