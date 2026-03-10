package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.model.Comment;
import org.elas.momentum.highlight.domain.port.in.GetCommentsUseCase;
import org.elas.momentum.highlight.domain.port.out.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCommentsService implements GetCommentsUseCase {

    private final CommentRepository commentRepository;

    public GetCommentsService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getComments(String highlightId) {
        return commentRepository.findByHighlightId(highlightId);
    }
}
