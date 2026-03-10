package org.elas.momentum.highlight.application.dto;

import org.elas.momentum.highlight.domain.model.Comment;
import java.time.Instant;

public record CommentResponse(
        String id,
        String highlightId,
        String authorId,
        String content,
        Instant createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(c.getId(), c.getHighlightId(),
                c.getAuthorId(), c.getContent(), c.getCreatedAt());
    }
}
