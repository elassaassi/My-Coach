package org.elas.momentum.coaching.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root — CoachingBooking Bounded Context.
 * No Spring annotations — pure domain class.
 */
public class CoachingBooking {

    private final BookingId id;
    private final OfferId offerId;
    private final String clientId;
    private final String clientType;
    private final Instant scheduledAt;
    private BookingStatus status;
    private final Instant createdAt;

    // ── Factory : creation ─────────────────────────────────────────────────────

    public static CoachingBooking create(OfferId offerId, String clientId,
                                         String clientType, Instant scheduledAt) {
        Objects.requireNonNull(offerId, "offerId must not be null");
        Objects.requireNonNull(clientId, "clientId must not be null");
        Objects.requireNonNull(clientType, "clientType must not be null");
        Objects.requireNonNull(scheduledAt, "scheduledAt must not be null");

        return new CoachingBooking(
                BookingId.generate(),
                offerId,
                clientId,
                clientType,
                scheduledAt,
                BookingStatus.PENDING,
                Instant.now()
        );
    }

    // ── Factory : reconstitution depuis persistance ────────────────────────────

    public static CoachingBooking reconstitute(BookingId id, OfferId offerId,
                                               String clientId, String clientType,
                                               Instant scheduledAt, BookingStatus status,
                                               Instant createdAt) {
        return new CoachingBooking(id, offerId, clientId, clientType, scheduledAt, status, createdAt);
    }

    // ── Private constructor ────────────────────────────────────────────────────

    private CoachingBooking(BookingId id, OfferId offerId, String clientId, String clientType,
                            Instant scheduledAt, BookingStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.offerId = Objects.requireNonNull(offerId);
        this.clientId = Objects.requireNonNull(clientId);
        this.clientType = Objects.requireNonNull(clientType);
        this.scheduledAt = Objects.requireNonNull(scheduledAt);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    // ── Business methods ───────────────────────────────────────────────────────

    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING bookings can be confirmed, current status: " + status);
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == BookingStatus.DONE) {
            throw new IllegalStateException("Cannot cancel a booking that is already DONE");
        }
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already CANCELLED");
        }
        this.status = BookingStatus.CANCELLED;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public BookingId getId() { return id; }
    public OfferId getOfferId() { return offerId; }
    public String getClientId() { return clientId; }
    public String getClientType() { return clientType; }
    public Instant getScheduledAt() { return scheduledAt; }
    public BookingStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
