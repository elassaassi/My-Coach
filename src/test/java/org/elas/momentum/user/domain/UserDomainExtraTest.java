package org.elas.momentum.user.domain;

import org.elas.momentum.user.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires complémentaires pour atteindre la couverture 80%.
 */
class UserDomainExtraTest {

    @Test
    @DisplayName("User.suspend passe le statut à SUSPENDED")
    void suspend_shouldTransitionToSuspended() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "A", "B");
        user.activate();
        user.suspend();
        assertThat(user.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    @DisplayName("User.delete passe le statut à DELETED")
    void delete_shouldTransitionToDeleted() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "A", "B");
        user.activate();
        user.delete();
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("User.getFullName retourne prénom + nom")
    void fullName_shouldConcatenate() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "Youness", "Benali");
        assertThat(user.getFullName()).isEqualTo("Youness Benali");
    }

    @Test
    @DisplayName("User.updateAvatar met à jour l'URL")
    void updateAvatar_shouldUpdate() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "A", "B");
        user.updateAvatar("https://cdn.momentum.app/avatar.jpg");
        assertThat(user.getAvatarUrl()).isEqualTo("https://cdn.momentum.app/avatar.jpg");
    }

    @Test
    @DisplayName("SportProfile contient les sports correctement")
    void sportProfile_shouldContainSports() {
        var sports = List.of(
                new SportLevel("football", Proficiency.INTERMEDIATE, 5),
                new SportLevel("tennis", Proficiency.BEGINNER, 2)
        );
        var profile = new SportProfile(sports, 33.9, -6.8, "Rabat", "MA");
        assertThat(profile.sports()).hasSize(2);
        assertThat(profile.city()).isEqualTo("Rabat");
        assertThat(profile.country()).isEqualTo("MA");
        assertThat(profile.latitude()).isEqualTo(33.9);
        assertThat(profile.longitude()).isEqualTo(-6.8);
    }

    @Test
    @DisplayName("SportProfile.empty() crée un profil vide")
    void sportProfile_empty() {
        var empty = SportProfile.empty();
        assertThat(empty.sports()).isEmpty();
    }

    @Test
    @DisplayName("Proficiency.ordinal() respecte l'ordre BEGINNER < INTERMEDIATE < ADVANCED < ELITE")
    void proficiency_order() {
        assertThat(Proficiency.BEGINNER.ordinal()).isLessThan(Proficiency.INTERMEDIATE.ordinal());
        assertThat(Proficiency.INTERMEDIATE.ordinal()).isLessThan(Proficiency.ADVANCED.ordinal());
        assertThat(Proficiency.ADVANCED.ordinal()).isLessThan(Proficiency.ELITE.ordinal());
    }

    @Test
    @DisplayName("User.reconstitute reconstruit tous les champs")
    void reconstitute_allFields() {
        var id = UserId.generate();
        var email = Email.of("test@test.com");
        var profile = SportProfile.empty();
        var user = User.reconstitute(id, email, "hash", "A", "B", "url", profile,
                UserStatus.ACTIVE, java.time.Instant.now(), java.time.Instant.now());
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getAvatarUrl()).isEqualTo("url");
    }
}
