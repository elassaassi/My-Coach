package org.elas.momentum.matching.domain.port.in;

import org.elas.momentum.matching.application.dto.MatchRequestCommand;
import org.elas.momentum.matching.application.dto.MatchResponse;

public interface RequestMatchUseCase {
    MatchResponse requestMatch(String requesterId, MatchRequestCommand command);
}
