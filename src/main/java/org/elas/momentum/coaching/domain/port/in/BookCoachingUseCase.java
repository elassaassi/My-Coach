package org.elas.momentum.coaching.domain.port.in;

import java.time.Instant;

public interface BookCoachingUseCase {
    record Command(String offerId, String clientId, String clientType, Instant scheduledAt) {}
    String book(Command command);
}
