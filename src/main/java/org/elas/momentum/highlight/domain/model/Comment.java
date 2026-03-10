package org.elas.momentum.highlight.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Comment {

    private final String id;
    private final String highlightId;
    private final String authorId;
    private final String content;
    private final Instant createdAt;

    public static Comment create(String highlightId, String authorId, String content) {
        Objects.requireNonNull(highlightId, "highlightId required");
        Objects.requireNonNull(authorId,    "authorId required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content required");
        if (content.length() > 300) throw new IllegalArgumentException("content max 300 chars");
        return new Comment(UUID.randomUUID().toString(), highlightId, authorId, content.trim(), Instant.now());
    }

    public static Comment reconstitute(String id, String highlightId, String authorId,
                                       String content, Instant createdAt) {
        return new Comment(id, highlightId, authorId, content, createdAt);
    }

    private Comment(String id, String highlightId, String authorId, String content, Instant createdAt) {
        this.id          = id;
        this.highlightId = highlightId;
        this.authorId    = authorId;
        this.content     = content;
        this.createdAt   = createdAt;
    }

    public String getId()          { return id; }
    public String getHighlightId() { return highlightId; }
    public String getAuthorId()    { return authorId; }
    public String getContent()     { return content; }
    public Instant getCreatedAt()  { return createdAt; }
}
