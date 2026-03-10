package org.elas.momentum.highlight.domain.port.out;

import org.elas.momentum.highlight.domain.model.Comment;
import java.util.List;
import java.util.Map;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByHighlightId(String highlightId);
    Map<String, Long> countByHighlightIds(List<String> highlightIds);
}
