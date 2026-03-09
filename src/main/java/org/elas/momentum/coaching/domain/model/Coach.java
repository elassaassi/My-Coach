package org.elas.momentum.coaching.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root — Coach Bounded Context.
 * No Spring annotations — pure domain class.
 */
public class Coach {

    private final CoachId id;
    private final String userId;
    private String bio;
    private final List<String> sports;
    private final List<String> certifications;
    private BigDecimal hourlyRate;
    private String currency;
    private boolean isAvailable;
    private double averageRating;
    private final Instant createdAt;

    // ── Factory : creation ─────────────────────────────────────────────────────

    public static Coach register(String userId, String bio, BigDecimal hourlyRate, String currency) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(hourlyRate, "hourlyRate must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        return new Coach(
                CoachId.generate(),
                userId,
                bio,
                new ArrayList<>(),
                new ArrayList<>(),
                hourlyRate,
                currency,
                true,
                0.0,
                Instant.now()
        );
    }

    // ── Factory : reconstitution depuis persistance ────────────────────────────

    public static Coach reconstitute(CoachId id, String userId, String bio,
                                     List<String> sports, List<String> certifications,
                                     BigDecimal hourlyRate, String currency,
                                     boolean isAvailable, double averageRating,
                                     Instant createdAt) {
        return new Coach(id, userId, bio,
                new ArrayList<>(sports),
                new ArrayList<>(certifications),
                hourlyRate, currency, isAvailable, averageRating, createdAt);
    }

    // ── Private constructor ────────────────────────────────────────────────────

    private Coach(CoachId id, String userId, String bio,
                  List<String> sports, List<String> certifications,
                  BigDecimal hourlyRate, String currency,
                  boolean isAvailable, double averageRating,
                  Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.bio = bio;
        this.sports = sports;
        this.certifications = certifications;
        this.hourlyRate = hourlyRate;
        this.currency = currency;
        this.isAvailable = isAvailable;
        this.averageRating = averageRating;
        this.createdAt = createdAt;
    }

    // ── Business methods ───────────────────────────────────────────────────────

    public void updateAvailability(boolean available) {
        this.isAvailable = available;
    }

    public void updateRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5, got: " + rating);
        }
        this.averageRating = rating;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public CoachId getId() { return id; }
    public String getUserId() { return userId; }
    public String getBio() { return bio; }
    public List<String> getSports() { return Collections.unmodifiableList(sports); }
    public List<String> getCertifications() { return Collections.unmodifiableList(certifications); }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public String getCurrency() { return currency; }
    public boolean isAvailable() { return isAvailable; }
    public double getAverageRating() { return averageRating; }
    public Instant getCreatedAt() { return createdAt; }
}
