package org.elas.momentum.matching.application.usecase;

import org.elas.momentum.matching.application.dto.MatchResponse;
import org.elas.momentum.matching.domain.model.MatchRequestId;
import org.elas.momentum.matching.domain.port.in.GetMatchUseCase;
import org.elas.momentum.matching.domain.port.out.MatchRequestRepository;
import org.elas.momentum.matching.infrastructure.persistence.MatchMapper;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetMatchService implements GetMatchUseCase {

    private final MatchRequestRepository repository;

    public GetMatchService(MatchRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public MatchResponse getById(String matchRequestId) {
        return repository.findById(MatchRequestId.of(matchRequestId))
                .map(MatchMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("MatchRequest", matchRequestId));
    }

    @Override
    public List<MatchResponse> getByUser(String userId) {
        return repository.findByRequesterId(userId).stream()
                .map(MatchMapper::toResponse)
                .toList();
    }
}
