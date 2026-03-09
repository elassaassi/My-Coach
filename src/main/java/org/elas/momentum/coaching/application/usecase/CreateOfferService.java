package org.elas.momentum.coaching.application.usecase;

import org.elas.momentum.coaching.domain.model.CoachId;
import org.elas.momentum.coaching.domain.model.CoachingOffer;
import org.elas.momentum.coaching.domain.port.in.CreateOfferUseCase;
import org.elas.momentum.coaching.domain.port.out.CoachRepository;
import org.elas.momentum.coaching.domain.port.out.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateOfferService implements CreateOfferUseCase {

    private final CoachRepository coachRepository;
    private final OfferRepository offerRepository;

    public CreateOfferService(CoachRepository coachRepository, OfferRepository offerRepository) {
        this.coachRepository = coachRepository;
        this.offerRepository = offerRepository;
    }

    @Override
    public String createOffer(Command command) {
        coachRepository.findById(command.coachId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Coach not found: " + command.coachId()));

        CoachingOffer offer = CoachingOffer.create(
                CoachId.of(command.coachId()),
                command.title(),
                command.description(),
                command.targetAudience(),
                command.sport(),
                command.durationMin(),
                command.price(),
                command.currency()
        );
        offerRepository.save(offer);
        return offer.id().value();
    }
}
