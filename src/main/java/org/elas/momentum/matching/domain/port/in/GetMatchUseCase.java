package org.elas.momentum.matching.domain.port.in;

import org.elas.momentum.matching.application.dto.MatchResponse;

import java.util.List;

public interface GetMatchUseCase {
    MatchResponse getById(String matchRequestId);
    List<MatchResponse> getByUser(String userId);
}
