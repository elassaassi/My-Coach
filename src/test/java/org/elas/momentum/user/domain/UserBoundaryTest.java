package org.elas.momentum.user.domain;

import org.elas.momentum.user.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class UserBoundaryTest {

    private User activeUser() {
        var user = User.register(Email.of("test@momentum.app"), "hash", "Youness", "Benali");
        user.activate();
        user.pullDomainEvents();
        return user;
    }

    // ── suspend() guard ───────────────────────────────────────────────────────

    @Test
    @DisplayName("suspend() sur PENDING lève IllegalStateException")
    void suspend_fromPending_throws() {
        var user = User.register(Email.of("a@b.com"), "h", "A", "B");
        assertThatThrownBy(user::suspend).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("suspend() sur SUSPENDED lève IllegalStateException")
    void suspend_alreadySuspended_throws() {
        var user = activeUser();
        user.suspend();
        assertThatThrownBy(user::suspend).isInstanceOf(IllegalStateException.class);
    }

    // ── delete() guard ────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() sur DELETED lève IllegalStateException")
    void delete_alreadyDeleted_throws() {
        var user = activeUser();
        user.delete();
        assertThatThrownBy(user::delete).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("delete() fonctionne depuis SUSPENDED")
    void delete_fromSuspended_succeeds() {
        var user = activeUser();
        user.suspend();
        user.delete();
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("delete() fonctionne depuis PENDING")
    void delete_fromPending_succeeds() {
        var user = User.register(Email.of("a@b.com"), "h", "A", "B");
        user.delete();
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
    }

    // ── updateProfile() null/blank guard ──────────────────────────────────────

    @Test
    @DisplayName("updateProfile() avec firstName null conserve l'ancien prénom")
    void updateProfile_nullFirstName_keepsOld() {
        var user = activeUser();
        var profile = new SportProfile(List.of(), 33.9, -6.8, "Rabat", "MA");
        user.updateProfile(profile, null, "Nouveau");
        assertThat(user.getFirstName()).isEqualTo("Youness");
        assertThat(user.getLastName()).isEqualTo("Nouveau");
    }

    @Test
    @DisplayName("updateProfile() avec lastName blank conserve l'ancien nom")
    void updateProfile_blankLastName_keepsOld() {
        var user = activeUser();
        var profile = new SportProfile(List.of(), 33.9, -6.8, "Rabat", "MA");
        user.updateProfile(profile, "NouveauPrenom", "   ");
        assertThat(user.getFirstName()).isEqualTo("NouveauPrenom");
        assertThat(user.getLastName()).isEqualTo("Benali");
    }

    @Test
    @DisplayName("updateProfile() null firstName ET null lastName conserve les deux")
    void updateProfile_bothNull_keepsAll() {
        var user = activeUser();
        var profile = new SportProfile(List.of(), 0.0, 0.0, "", "");
        user.updateProfile(profile, null, null);
        assertThat(user.getFirstName()).isEqualTo("Youness");
        assertThat(user.getLastName()).isEqualTo("Benali");
    }

    // ── registerViaOAuth() ────────────────────────────────────────────────────

    @Test
    @DisplayName("registerViaOAuth() crée un compte directement ACTIVE")
    void registerViaOAuth_isDirectlyActive() {
        var user = User.registerViaOAuth(Email.of("oauth@momentum.app"), "Karim", "Benzema");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getPasswordHash()).isEqualTo("OAUTH2:NO_PASSWORD_HASH");
    }

    @Test
    @DisplayName("registerViaOAuth() émet un UserRegisteredEvent")
    void registerViaOAuth_emitsEvent() {
        var user = User.registerViaOAuth(Email.of("oauth@momentum.app"), "K", "B");
        var events = user.pullDomainEvents();
        assertThat(events).hasSize(1);
    }

    // ── reconstitute() ────────────────────────────────────────────────────────

    @Test
    @DisplayName("reconstitute() avec sportProfile null utilise un profil vide")
    void reconstitute_nullSportProfile_usesEmpty() {
        var id = UserId.generate();
        var user = User.reconstitute(id, Email.of("t@t.com"), "h", "A", "B",
                null, null, UserStatus.ACTIVE,
                java.time.Instant.now(), java.time.Instant.now());
        assertThat(user.getSportProfile()).isNotNull();
        assertThat(user.getSportProfile().sports()).isEmpty();
    }

    // ── UserId ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("UserId.of() restitue la valeur exacte")
    void userId_of_restoresValue() {
        var id = UserId.of("user-123");
        assertThat(id.value()).isEqualTo("user-123");
    }

    // ── Email ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Email.toString() retourne la valeur normalisée")
    void email_toString_returnsValue() {
        var email = Email.of("TEST@momentum.APP");
        assertThat(email.toString()).isEqualTo("test@momentum.app");
    }

    // ── SportProfile.getSportLevel() ──────────────────────────────────────────

    @Test
    @DisplayName("SportProfile.getSportLevel() insensible à la casse")
    void sportProfile_getSportLevel_caseInsensitive() {
        var sports = List.of(new SportLevel("Football", Proficiency.INTERMEDIATE, 5));
        var profile = new SportProfile(sports, 0.0, 0.0, "", "");
        assertThat(profile.getSportLevel("football")).isPresent();
        assertThat(profile.getSportLevel("FOOTBALL")).isPresent();
        assertThat(profile.getSportLevel("tennis")).isEmpty();
    }
}