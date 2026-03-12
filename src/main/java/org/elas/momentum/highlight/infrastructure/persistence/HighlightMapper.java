package org.elas.momentum.highlight.infrastructure.persistence;

import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.HighlightId;
import org.elas.momentum.highlight.domain.model.HighlightOfDay;
import org.elas.momentum.highlight.domain.model.MediaType;

final class HighlightMapper {

    private HighlightMapper() {}

    static Highlight toDomain(HighlightEntity e) {
        return Highlight.reconstitute(
                HighlightId.of(e.getId()),
                e.getPublisherId(),
                e.getMediaUrl(),
                MediaType.valueOf(e.getMediaType()),
                e.getCaption(),
                e.getSport(),
                e.getLatitude(),
                e.getLongitude(),
                e.getLikeCount(),
                e.isHighlightOfDay(),
                e.getPublishedAt(),
                e.getArchivedAt(),
                e.getEditedAt()
        );
    }

    static HighlightEntity toEntity(Highlight h) {
        var e = new HighlightEntity();
        e.setId(h.getId().value());
        e.setPublisherId(h.getPublisherId());
        e.setMediaUrl(h.getMediaUrl());
        e.setMediaType(h.getMediaType().name());
        e.setCaption(h.getCaption());
        e.setSport(h.getSport());
        e.setLatitude(h.getLatitude());
        e.setLongitude(h.getLongitude());
        e.setLikeCount(h.getLikeCount());
        e.setHighlightOfDay(h.isHighlightOfDay());
        e.setPublishedAt(h.getPublishedAt());
        e.setArchivedAt(h.getArchivedAt());
        e.setEditedAt(h.getEditedAt());
        return e;
    }

    static HighlightOfDay toDomain(HighlightOfDayEntity e) {
        return new HighlightOfDay(e.getDate(), e.getHighlightId(), e.getSelectedAt());
    }

    static HighlightOfDayEntity toEntity(HighlightOfDay hod) {
        var e = new HighlightOfDayEntity();
        e.setDate(hod.date());
        e.setHighlightId(hod.highlightId());
        e.setSelectedAt(hod.selectedAt());
        return e;
    }
}
