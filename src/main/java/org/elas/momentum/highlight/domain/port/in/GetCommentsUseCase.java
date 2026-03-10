package org.elas.momentum.highlight.domain.port.in;

import org.elas.momentum.highlight.domain.model.Comment;
import java.util.List;

public interface GetCommentsUseCase {
    List<Comment> getComments(String highlightId);
}
