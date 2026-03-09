package org.elas.momentum.coaching.domain.port.out;

import org.elas.momentum.coaching.domain.model.CoachingBooking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    void save(CoachingBooking booking);
    Optional<CoachingBooking> findById(String id);
    List<CoachingBooking> findByClientId(String clientId);
}
