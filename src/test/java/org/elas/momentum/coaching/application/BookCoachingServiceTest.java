package org.elas.momentum.coaching.application;

import org.elas.momentum.coaching.application.usecase.BookCoachingService;
import org.elas.momentum.coaching.domain.model.CoachingOffer;
import org.elas.momentum.coaching.domain.port.in.BookCoachingUseCase.Command;
import org.elas.momentum.coaching.domain.port.out.BookingRepository;
import org.elas.momentum.coaching.domain.port.out.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCoachingServiceTest {

    @Mock OfferRepository offerRepository;
    @Mock BookingRepository bookingRepository;

    BookCoachingService service;

    @BeforeEach
    void setUp() {
        service = new BookCoachingService(offerRepository, bookingRepository);
    }

    @Test
    @DisplayName("book() nominal — crée la réservation et retourne un id")
    void book_nominal_savesAndReturnsId() {
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(mock(CoachingOffer.class)));

        String id = service.book(new Command("offer-1", "client-1", "INDIVIDUAL",
                Instant.now().plusSeconds(3600)));

        assertThat(id).isNotBlank();
        verify(bookingRepository).save(any());
    }

    @Test
    @DisplayName("book() offer inexistante → IllegalArgumentException")
    void book_offerNotFound_throws() {
        when(offerRepository.findById("bad-offer")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.book(
                new Command("bad-offer", "client-1", "INDIVIDUAL", Instant.now().plusSeconds(3600))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bad-offer");

        verify(bookingRepository, never()).save(any());
    }
}