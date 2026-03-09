package org.elas.momentum.coaching.domain.model;

import java.util.UUID;

public record BookingId(String value) {
    public static BookingId generate() { return new BookingId(UUID.randomUUID().toString()); }
    public static BookingId of(String value) { return new BookingId(value); }
}
