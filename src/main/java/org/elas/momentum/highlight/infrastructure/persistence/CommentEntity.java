package org.elas.momentum.highlight.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments", schema = "highlight")
class CommentEntity {

    @Id
    private String id;

    @Column(name = "highlight_id", nullable = false)
    private String highlightId;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(name = "content", nullable = false, length = 300)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public String getId()          { return id; }
    public void setId(String id)   { this.id = id; }
    public String getHighlightId() { return highlightId; }
    public void setHighlightId(String v) { this.highlightId = v; }
    public String getAuthorId()    { return authorId; }
    public void setAuthorId(String v)    { this.authorId = v; }
    public String getContent()     { return content; }
    public void setContent(String v)     { this.content = v; }
    public Instant getCreatedAt()  { return createdAt; }
    public void setCreatedAt(Instant v)  { this.createdAt = v; }
}
