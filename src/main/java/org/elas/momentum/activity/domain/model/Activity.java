package org.elas.momentum.activity.domain.model;

import org.elas.momentum.activity.domain.exception.ActivityFullException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root — Activity Bounded Context.
 */
public class Activity {

    private final ActivityId id;
    private final String organizerId;
    private String title;
    private String description;
    private String sport;
    private String requiredLevel;
    private Location location;
    private Instant scheduledAt;
    private int maxParticipants;
    private ActivityStatus status;
    private final List<Participant> participants = new ArrayList<>();
    private final Instant createdAt;
    private Instant updatedAt;

    // ── Factory : création ────────────────────────────────────────────────────

    public static Activity create(String organizerId, String title, String sport,
                                  String requiredLevel, Location location,
                                  Instant scheduledAt, int maxParticipants) {
        Objects.requireNonNull(organizerId);
        Objects.requireNonNull(title);
        Objects.requireNonNull(sport);
        Objects.requireNonNull(location);
        Objects.requireNonNull(scheduledAt);
        if (maxParticipants < 2) throw new IllegalArgumentException("An activity needs at least 2 participants");

        return new Activity(ActivityId.generate(), organizerId, title, sport,
                requiredLevel, location, scheduledAt, maxParticipants,
                ActivityStatus.OPEN, List.of(Participant.of(organizerId)),
                Instant.now(), Instant.now());
    }

    // ── Factory : reconstitution depuis persistance (bypass des règles métier) ──

    public static Activity reconstitute(ActivityId id, String organizerId, String title,
                                        String description, String sport, String requiredLevel,
                                        Location location, Instant scheduledAt, int maxParticipants,
                                        ActivityStatus status, List<Participant> participants,
                                        Instant createdAt, Instant updatedAt) {
        var a = new Activity(id, organizerId, title, sport, requiredLevel,
                location, scheduledAt, maxParticipants, status, participants, createdAt, updatedAt);
        a.description = description;
        return a;
    }

    // ── Constructor interne ───────────────────────────────────────────────────

    private Activity(ActivityId id, String organizerId, String title, String sport,
                     String requiredLevel, Location location, Instant scheduledAt,
                     int maxParticipants, ActivityStatus status,
                     List<Participant> initialParticipants, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.organizerId = Objects.requireNonNull(organizerId);
        this.title = title;
        this.sport = sport;
        this.requiredLevel = requiredLevel;
        this.location = location;
        this.scheduledAt = scheduledAt;
        this.maxParticipants = maxParticipants;
        this.status = status;
        this.participants.addAll(initialParticipants);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void join(String userId) {
        Objects.requireNonNull(userId);
        if (isParticipant(userId)) {
            throw new IllegalStateException("User is already a participant");
        }
        if (status == ActivityStatus.FULL || participants.size() >= maxParticipants) {
            throw new ActivityFullException(id.value());
        }
        if (status != ActivityStatus.OPEN) {
            throw new IllegalStateException("Activity is not open for joining");
        }



        participants.add(Participant.of(userId));

        if (participants.size() >= maxParticipants) {
            status = ActivityStatus.FULL;
        }
        updatedAt = Instant.now();
    }

    public void leave(String userId) {
        if (!isParticipant(userId)) {
            throw new IllegalStateException("User is not a participant");
        }


        if (userId.equals(organizerId)) {
            throw new IllegalStateException("Organizer cannot leave their own activity");
        }
        participants.removeIf(

                p -> p.userId().equals(userId));
        if (status == ActivityStatus.FULL) {
            status = ActivityStatus.OPEN;
        }
        updatedAt = Instant.now();
    }

    public void complete() {
        if (status == ActivityStatus.COMPLETED || status == ActivityStatus.CANCELLED) {
            throw new IllegalStateException("Activity is already " + status);
        }
        this.status = ActivityStatus.COMPLETED;
        updatedAt = Instant.now();
    }



    public void startOngoing() {
        if (status != ActivityStatus.OPEN && status != ActivityStatus.FULL) {
            throw new IllegalStateException("Activity cannot be started from status: " + status);
        }
        this.status = ActivityStatus.ONGOING;
        updatedAt = Instant.now();
    }

    public void cancel() {
        if (status == ActivityStatus.COMPLETED || status == ActivityStatus.CANCELLED) {
            throw new IllegalStateException("Activity is already " + status);
        }
        this.status = ActivityStatus.CANCELLED;
        updatedAt = Instant.now();
    }

    public boolean isParticipant(String userId) {
        return participants.stream().anyMatch(p -> p.userId().equals(userId));
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public ActivityId getId() { return id; }
    public String getOrganizerId() { return organizerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSport() { return sport; }
    public String getRequiredLevel() { return requiredLevel; }
    public Location getLocation() { return location; }
    public Instant getScheduledAt() { return scheduledAt; }
    public int getMaxParticipants() { return maxParticipants; }
    public ActivityStatus getStatus() { return status; }
    public List<Participant> getParticipants() { return Collections.unmodifiableList(participants); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public int getCurrentParticipantsCount() { return participants.size(); }
    public boolean isFull() { return participants.size() >= maxParticipants; }
}
