package org.elas.momentum.matching.application.usecase;

import org.elas.momentum.matching.application.dto.MatchRequestCommand;
import org.elas.momentum.matching.application.dto.MatchResponse;
import org.elas.momentum.matching.domain.model.MatchCriteria;
import org.elas.momentum.matching.domain.model.MatchOutcome;
import org.elas.momentum.matching.domain.model.MatchRequest;
import org.elas.momentum.matching.domain.port.in.RequestMatchUseCase;
import org.elas.momentum.matching.domain.port.out.MatchRequestRepository;
import org.elas.momentum.matching.domain.port.out.UserProfilePort;
import org.elas.momentum.matching.domain.service.MatchingAlgorithm;
import org.elas.momentum.matching.infrastructure.persistence.MatchMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RequestMatchService implements RequestMatchUseCase {

    private final MatchRequestRepository repository;
    private final UserProfilePort userProfilePort;
    private final MatchingAlgorithm matchingAlgorithm;

    public RequestMatchService(MatchRequestRepository repository,
                               UserProfilePort userProfilePort) {
        this.repository = repository;
        this.userProfilePort = userProfilePort;
        this.matchingAlgorithm = new MatchingAlgorithm();
    }

    @Override
    public MatchResponse requestMatch(String requesterId, MatchRequestCommand command) {
        var criteria = MatchCriteria.of(
                command.sport(),
                command.proficiency(),
                command.latitude(),
                command.longitude(),
                command.maxDistanceKm()
        );

        MatchRequest matchRequest = MatchRequest.create(requesterId, criteria);

        // Run matching algorithm
        var candidates = userProfilePort.findCandidates(command.sport(), requesterId);
        MatchOutcome outcome = matchingAlgorithm.findBestMatch(criteria, candidates);

        switch (outcome) {
            case MatchOutcome.Found(var uid, var score, var dist) ->
                    matchRequest.matchFound(uid, score);
            case MatchOutcome.NoMatch(var reason) ->
                    matchRequest.noMatchFound();
            case MatchOutcome.Pending(var id) ->
                    { /* already pending by default */ }
        }

        MatchRequest saved = repository.save(matchRequest);
        return MatchMapper.toResponse(saved);
    }
}
