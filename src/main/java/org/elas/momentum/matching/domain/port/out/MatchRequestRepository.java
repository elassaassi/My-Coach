package org.elas.momentum.matching.domain.port.out;

import org.elas.momentum.matching.domain.model.MatchRequest;
import org.elas.momentum.matching.domain.model.MatchRequestId;

import java.util.List;
import java.util.Optional;

public interface MatchRequestRepository {
    MatchRequest save(MatchRequest matchRequest);
    Optional<MatchRequest> findById(MatchRequestId id);
    List<MatchRequest> findByRequesterId(String requesterId);
}
