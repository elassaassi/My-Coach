package org.elas.momentum.coaching.domain.port.in;

import java.math.BigDecimal;

public interface RegisterCoachUseCase {
    record Command(String userId, String bio, BigDecimal hourlyRate, String currency) {}
    String register(Command command);
}
