package org.elas.momentum.highlight.infrastructure.persistence;

import org.elas.momentum.highlight.domain.model.Comment;
import org.elas.momentum.highlight.domain.port.out.CommentRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
class CommentPersistenceAdapter implements CommentRepository {

    private final CommentJpaRepository jpa;

    CommentPersistenceAdapter(CommentJpaRepository jpa) { this.jpa = jpa; }

    @Override
    public Comment save(Comment c) {
        CommentEntity e = new CommentEntity();
        e.setId(c.getId());
        e.setHighlightId(c.getHighlightId());
        e.setAuthorId(c.getAuthorId());
        e.setContent(c.getContent());
        e.setCreatedAt(c.getCreatedAt());
        jpa.save(e);
        return c;
    }

    @Override
    public List<Comment> findByHighlightId(String highlightId) {
        return jpa.findByHighlightIdOrderByCreatedAtAsc(highlightId).stream()
                .map(e -> Comment.reconstitute(e.getId(), e.getHighlightId(),
                        e.getAuthorId(), e.getContent(), e.getCreatedAt()))
                .toList();
    }

    @Override
    public Map<String, Long> countByHighlightIds(List<String> ids) {
        if (ids.isEmpty()) return Map.of();
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : jpa.countByHighlightIds(ids)) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }
}
