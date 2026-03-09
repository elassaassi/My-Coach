package org.elas.momentum.matching.infrastructure.persistence;

import org.elas.momentum.matching.application.dto.MatchResponse;
import org.elas.momentum.matching.domain.model.MatchCriteria;
import org.elas.momentum.matching.domain.model.MatchRequest;
import org.elas.momentum.matching.domain.model.MatchRequestId;
import org.elas.momentum.matching.domain.model.MatchStatus;

public final class MatchMapper {

    private MatchMapper() {}

    public static MatchRequest toDomain(MatchRequestEntity e) {
        var criteria = MatchCriteria.of(e.getSport(), e.getProficiency(),
                e.getLatitude(), e.getLongitude(), e.getMaxDistanceKm());

        var mr = new MatchRequest(MatchRequestId.of(e.getId()), e.getRequesterId(), criteria);
        if (e.getMatchedUserId() != null) {
            mr.matchFound(e.getMatchedUserId(), e.getMatchScore());
        } else if (MatchStatus.NO_MATCH.name().equals(e.getStatus())) {
            mr.noMatchFound();
        }
        return mr;
    }

    public static MatchRequestEntity toEntity(MatchRequest mr) {
        var e = new MatchRequestEntity();
        e.setId(mr.getId().value());
        e.setRequesterId(mr.getRequesterId());
        e.setSport(mr.getCriteria().sport());
        e.setProficiency(mr.getCriteria().proficiency());
        e.setLatitude(mr.getCriteria().latitude());
        e.setLongitude(mr.getCriteria().longitude());
        e.setMaxDistanceKm(mr.getCriteria().maxDistanceKm());
        e.setStatus(mr.getStatus().name());
        e.setMatchedUserId(mr.getMatchedUserId());
        e.setMatchScore(mr.getMatchScore());
        e.setCreatedAt(mr.getCreatedAt());
        e.setUpdatedAt(mr.getUpdatedAt());
        return e;
    }

    public static MatchResponse toResponse(MatchRequest mr) {
        return new MatchResponse(
                mr.getId().value(),
                mr.getRequesterId(),
                mr.getCriteria().sport(),
                mr.getCriteria().proficiency(),
                mr.getCriteria().latitude(),
                mr.getCriteria().longitude(),
                mr.getCriteria().maxDistanceKm(),
                mr.getStatus().name(),
                mr.getMatchedUserId(),
                mr.getMatchScore(),
                mr.getCreatedAt()
        );
    }
}
