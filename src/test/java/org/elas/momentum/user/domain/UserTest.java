package org.elas.momentum.user.domain;

import org.elas.momentum.user.domain.event.UserRegisteredEvent;
import org.elas.momentum.user.domain.exception.InvalidEmailException;
import org.elas.momentum.user.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires domaine — <50ms, aucun Spring Context, aucune DB.
 */
class UserTest {

    @Test
    @DisplayName("Inscription crée un user avec statut PENDING et émet un événement")
    void register_shouldCreateUserWithPendingStatus_andEmitEvent() {
        var email = Email.of("youness@momentum.app");
        var user = User.register(email, "hashed_password", "Youness", "Test");

        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getFirstName()).isEqualTo("Youness");

        var events = user.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(UserRegisteredEvent.class);

        // pullDomainEvents vide les événements
        assertThat(user.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Email invalide lève une exception à la construction")
    void email_withInvalidFormat_shouldThrow() {
        assertThatThrownBy(() -> Email.of("not-an-email"))
                .isInstanceOf(InvalidEmailException.class);

        assertThatThrownBy(() -> Email.of(""))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    @DisplayName("Email est normalisé en minuscules")
    void email_shouldBeNormalized() {
        var email = Email.of("  USER@Momentum.APP  ");
        assertThat(email.value()).isEqualTo("user@momentum.app");
    }

    @Test
    @DisplayName("Activation d'un user PENDING passe au statut ACTIVE")
    void activate_shouldTransitionToActive() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "A", "B");
        user.activate();
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Double activation lève une IllegalStateException")
    void activate_twice_shouldThrow() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "A", "B");
        user.activate();
        assertThatThrownBy(user::activate).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Mise à jour du profil sportif émet un ProfileUpdatedEvent")
    void updateProfile_shouldEmitEvent() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "A", "B");
        user.activate();
        user.pullDomainEvents(); // vider les événements précédents

        var sports = List.of(new SportLevel("football", Proficiency.INTERMEDIATE, 5));
        var profile = new SportProfile(sports, 33.9, -6.8, "Rabat", "MA");
        user.updateProfile(profile, "Youness", "Updated");

        var events = user.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(user.getSportProfile().sports()).hasSize(1);
        assertThat(user.getSportProfile().sports().getFirst().sport()).isEqualTo("football");
    }

    @Test
    @DisplayName("Proficiency.isCompatibleWith retourne true pour niveaux adjacents")
    void proficiency_compatibility() {
        assertThat(Proficiency.BEGINNER.isCompatibleWith(Proficiency.INTERMEDIATE)).isTrue();
        assertThat(Proficiency.BEGINNER.isCompatibleWith(Proficiency.ADVANCED)).isFalse();
        assertThat(Proficiency.ELITE.isCompatibleWith(Proficiency.ADVANCED)).isTrue();
        assertThat(Proficiency.INTERMEDIATE.isCompatibleWith(Proficiency.INTERMEDIATE)).isTrue();
    }

    @Test
    @DisplayName("UserId.generate() produit des IDs uniques")
    void userId_generate_shouldBeUnique() {
        var id1 = UserId.generate();
        var id2 = UserId.generate();
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.value()).isNotBlank();
    }
}
