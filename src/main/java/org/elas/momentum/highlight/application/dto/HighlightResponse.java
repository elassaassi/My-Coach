package org.elas.momentum.highlight.application.dto;

import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.MediaType;

import java.time.Instant;

public record HighlightResponse(
        String id,
        String publisherId,
        String mediaUrl,
        MediaType mediaType,
        String caption,
        String sport,
        Double latitude,
        Double longitude,
        int likeCount,
        int commentCount,
        boolean isHighlightOfDay,
        Instant publishedAt
) {
    public static HighlightResponse from(Highlight h) {
        return from(h, 0);
    }

    public static HighlightResponse from(Highlight h, int commentCount) {
        return new HighlightResponse(
                h.getId().value(),
                h.getPublisherId(),
                h.getMediaUrl(),
                h.getMediaType(),
                h.getCaption(),
                h.getSport(),
                h.getLatitude(),
                h.getLongitude(),
                h.getLikeCount(),
                commentCount,
                h.isHighlightOfDay(),
                h.getPublishedAt()
        );
    }
}
