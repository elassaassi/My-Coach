package org.elas.momentum.coaching.application.usecase;

import org.elas.momentum.coaching.domain.model.CoachingBooking;
import org.elas.momentum.coaching.domain.model.OfferId;
import org.elas.momentum.coaching.domain.port.in.BookCoachingUseCase;
import org.elas.momentum.coaching.domain.port.out.BookingRepository;
import org.elas.momentum.coaching.domain.port.out.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookCoachingService implements BookCoachingUseCase {

    private final OfferRepository offerRepository;
    private final BookingRepository bookingRepository;

    public BookCoachingService(OfferRepository offerRepository, BookingRepository bookingRepository) {
        this.offerRepository = offerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public String book(Command command) {
        offerRepository.findById(command.offerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Offer not found: " + command.offerId()));

        CoachingBooking booking = CoachingBooking.create(
                OfferId.of(command.offerId()),
                command.clientId(),
                command.clientType(),
                command.scheduledAt()
        );
        bookingRepository.save(booking);
        return booking.getId().value();
    }
}
