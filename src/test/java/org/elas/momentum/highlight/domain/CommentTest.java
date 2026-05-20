package org.elas.momentum.highlight.domain;

import org.elas.momentum.highlight.domain.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("Comment.create() nominal — ID généré, contenu trimmé")
    void create_nominal() {
        var comment = Comment.create("highlight-1", "user-1", "  Super highlight !  ");
        assertThat(comment.getId()).isNotBlank();
        assertThat(comment.getHighlightId()).isEqualTo("highlight-1");
        assertThat(comment.getAuthorId()).isEqualTo("user-1");
        assertThat(comment.getContent()).isEqualTo("Super highlight !");
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Comment.create() contenu null lève IllegalArgumentException")
    void create_nullContent_throws() {
        assertThatThrownBy(() -> Comment.create("h-1", "u-1", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Comment.create() contenu vide lève IllegalArgumentException")
    void create_blankContent_throws() {
        assertThatThrownBy(() -> Comment.create("h-1", "u-1", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Comment.create("h-1", "u-1", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Comment.create() contenu > 300 chars lève IllegalArgumentException")
    void create_tooLong_throws() {
        var tooLong = "x".repeat(301);
        assertThatThrownBy(() -> Comment.create("h-1", "u-1", tooLong))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Comment.create() contenu de 300 chars exactement est accepté")
    void create_exactLimit_accepted() {
        var exactly300 = "x".repeat(300);
        var comment = Comment.create("h-1", "u-1", exactly300);
        assertThat(comment.getContent()).hasSize(300);
    }

    @Test
    @DisplayName("Comment.create() highlightId null lève NullPointerException")
    void create_nullHighlightId_throws() {
        assertThatThrownBy(() -> Comment.create(null, "u-1", "ok"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Comment.create() authorId null lève NullPointerException")
    void create_nullAuthorId_throws() {
        assertThatThrownBy(() -> Comment.create("h-1", null, "ok"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Comment.reconstitute() préserve tous les champs sans modification")
    void reconstitute_preservesAllFields() {
        var now = Instant.now();
        var comment = Comment.reconstitute("id-42", "h-1", "u-1", "contenu original", now);
        assertThat(comment.getId()).isEqualTo("id-42");
        assertThat(comment.getHighlightId()).isEqualTo("h-1");
        assertThat(comment.getAuthorId()).isEqualTo("u-1");
        assertThat(comment.getContent()).isEqualTo("contenu original");
        assertThat(comment.getCreatedAt()).isEqualTo(now);
    }
}