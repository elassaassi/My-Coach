package org.elas.momentum.coaching.infrastructure.persistence;

import org.elas.momentum.coaching.domain.model.CoachingBooking;
import org.elas.momentum.coaching.domain.port.out.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class BookingPersistenceAdapter implements BookingRepository {

    private final CoachingBookingJpaRepository jpaRepository;

    BookingPersistenceAdapter(CoachingBookingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(CoachingBooking booking) {
        jpaRepository.save(CoachingMapper.toEntity(booking));
    }

    @Override
    public Optional<CoachingBooking> findById(String id) {
        return jpaRepository.findById(id).map(CoachingMapper::toDomain);
    }

    @Override
    public List<CoachingBooking> findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId)
                .stream()
                .map(CoachingMapper::toDomain)
                .toList();
    }
}
