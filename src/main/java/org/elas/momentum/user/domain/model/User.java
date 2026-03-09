package org.elas.momentum.user.domain.model;

import org.elas.momentum.shared.domain.DomainEvent;
import org.elas.momentum.user.domain.event.ProfileUpdatedEvent;
import org.elas.momentum.user.domain.event.UserRegisteredEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root — User Bounded Context.
 * Aucune annotation Spring. Aucune dépendance externe.
 */
public class User {

    private final UserId id;
    private Email email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private SportProfile sportProfile;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ── Factory : inscription ────────────────────────────────────────────────

    public static User register(Email email, String passwordHash, String firstName, String lastName) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(passwordHash);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);

        var user = new User(UserId.generate(), email, passwordHash, firstName, lastName);
        user.domainEvents.add(UserRegisteredEvent.of(user.id, user.email, user.firstName, user.lastName));
        return user;
    }

    // ── Factory : inscription via OAuth2 social login ────────────────────────

    /**
     * Crée un compte utilisateur via OAuth2 (Google, Facebook...).
     * L'identité étant vérifiée par le provider, le compte est directement ACTIVE.
     * Aucun mot de passe utilisable n'est stocké — connexion OAuth2 uniquement.
     */
    public static User registerViaOAuth(Email email, String firstName, String lastName) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);

        var user = new User(UserId.generate(), email, "OAUTH2:NO_PASSWORD_HASH", firstName, lastName);
        user.activate(); // identité vérifiée par le provider OAuth2
        user.domainEvents.add(UserRegisteredEvent.of(user.id, user.email, user.firstName, user.lastName));
        return user;
    }

    // ── Factory : reconstitution depuis persistance ───────────────────────────

    public static User reconstitute(UserId id, Email email, String passwordHash,
                                    String firstName, String lastName, String avatarUrl,
                                    SportProfile sportProfile, UserStatus status,
                                    Instant createdAt, Instant updatedAt) {
        var user = new User(id, email, passwordHash, firstName, lastName);
        user.avatarUrl = avatarUrl;
        user.sportProfile = sportProfile != null ? sportProfile : SportProfile.empty();
        user.status = status;
        // Override timestamps set by constructor
        return user;
    }

    // ── Constructor interne ───────────────────────────────────────────────────

    public User(UserId id, Email email, String passwordHash, String firstName, String lastName) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.sportProfile = SportProfile.empty();
        this.status = UserStatus.PENDING_VERIFICATION;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void activate() {
        if (status != UserStatus.PENDING_VERIFICATION) {
            throw new IllegalStateException("Can only activate a pending user");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(SportProfile sportProfile, String firstName, String lastName) {
        Objects.requireNonNull(sportProfile);
        this.sportProfile = sportProfile;
        if (firstName != null && !firstName.isBlank()) this.firstName = firstName;
        if (lastName != null && !lastName.isBlank()) this.lastName = lastName;
        this.updatedAt = Instant.now();
        domainEvents.add(ProfileUpdatedEvent.of(id));
    }

    public void suspend() {
        if (status != UserStatus.ACTIVE) {
            throw new IllegalStateException("Can only suspend an active user");
        }
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        if (status == UserStatus.DELETED) {
            throw new IllegalStateException("User is already deleted");
        }
        this.status = UserStatus.DELETED;
        this.updatedAt = Instant.now();
    }

    public void updateAvatar(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public List<DomainEvent> pullDomainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public UserId getId() { return id; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAvatarUrl() { return avatarUrl; }
    public SportProfile getSportProfile() { return sportProfile; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public String getFullName() { return firstName + " " + lastName; }
}
