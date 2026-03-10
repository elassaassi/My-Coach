package org.elas.momentum.highlight.domain.port.in;

public interface AddCommentUseCase {
    record Command(String highlightId, String authorId, String content) {}
    String addComment(Command command);
}
