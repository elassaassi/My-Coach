package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.model.Comment;
import org.elas.momentum.highlight.domain.port.in.AddCommentUseCase;
import org.elas.momentum.highlight.domain.port.out.CommentRepository;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.elas.momentum.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AddCommentService implements AddCommentUseCase {

    private final CommentRepository commentRepository;
    private final HighlightRepository highlightRepository;

    public AddCommentService(CommentRepository commentRepository,
                             HighlightRepository highlightRepository) {
        this.commentRepository  = commentRepository;
        this.highlightRepository = highlightRepository;
    }

    @Override
    public String addComment(Command command) {
        highlightRepository.findById(command.highlightId())
                .orElseThrow(() -> new NotFoundException("Highlight", command.highlightId()));
        Comment comment = Comment.create(command.highlightId(), command.authorId(), command.content());
        commentRepository.save(comment);
        return comment.getId();
    }
}
