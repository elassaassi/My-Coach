package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityResult;
import org.elas.momentum.activity.domain.exception.ActivityNotFoundException;
import org.elas.momentum.activity.domain.model.ActivityId;
import org.elas.momentum.activity.domain.port.in.GetActivityUseCase;
import org.elas.momentum.activity.domain.port.out.ActivityRepository;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMapper;
import org.elas.momentum.user.UserModuleAPI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetActivityService implements GetActivityUseCase {

    private final ActivityRepository repository;
    private final UserModuleAPI      userModuleAPI;

    public GetActivityService(ActivityRepository repository, UserModuleAPI userModuleAPI) {
        this.repository    = repository;
        this.userModuleAPI = userModuleAPI;
    }

    /** Enriches participant DTOs with resolved first/last names. */
    private ActivityResult withParticipantNames(ActivityResult result) {
        var enriched = result.participants().stream()
                .map(p -> {
                    var summary = userModuleAPI.findById(p.userId());
                    return new ActivityResult.ParticipantDto(
                            p.userId(), p.joinedAt(),
                            summary.map(s -> s.firstName()).orElse(null),
                            summary.map(s -> s.lastName()).orElse(null));
                })
                .toList();
        return new ActivityResult(
                result.id(), result.organizerId(), result.title(), result.description(),
                result.sport(), result.requiredLevel(), result.location(), result.scheduledAt(),
                result.maxParticipants(), result.currentParticipantsCount(), result.status(),
                enriched, result.createdAt());
    }

    @Override
    public ActivityResult getById(String activityId) {
        return repository.findById(ActivityId.of(activityId))
                .map(ActivityMapper::toResult)
                .map(this::withParticipantNames)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
    }

    @Override
    public List<ActivityResult> search(String sport, String city, String status, Instant dateFrom, Instant dateTo, int page, int size) {
        return repository.search(sport, city, status, dateFrom, dateTo, page, size).stream()
                .map(ActivityMapper::toResult)
                .toList();
    }

    @Override
    public List<ActivityResult> getByUser(String userId) {
        var asOrganizer   = repository.findByOrganizerId(userId);
        var asParticipant = repository.findByParticipantId(userId);

        // Deduplicate by ID (Activity has no equals/hashCode; organizer is also a participant)
        return java.util.stream.Stream.concat(asOrganizer.stream(), asParticipant.stream())
                .collect(java.util.stream.Collectors.toMap(
                        a -> a.getId().value(),
                        a -> a,
                        (a, b) -> a,           // keep first occurrence
                        LinkedHashMap::new))
                .values().stream()
                .map(ActivityMapper::toResult)
                .toList();
    }
}
