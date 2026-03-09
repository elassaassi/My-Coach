package org.elas.momentum.scouting.infrastructure;

import org.elas.momentum.rating.RatingSubmittedEvent;
import org.elas.momentum.scouting.domain.port.in.IndexTalentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class RatingEventListener {

    private static final Logger log = LoggerFactory.getLogger(RatingEventListener.class);

    private final IndexTalentUseCase indexTalentUseCase;

    public RatingEventListener(IndexTalentUseCase indexTalentUseCase) {
        this.indexTalentUseCase = indexTalentUseCase;
    }

    @ApplicationModuleListener
    public void onRatingSubmitted(RatingSubmittedEvent event) {
        log.info("Received RatingSubmittedEvent for user {} with proScore {}",
                event.ratedUserId(), event.proScore());
        indexTalentUseCase.index(event.ratedUserId(), "all", event.proScore());
    }
}
