package org.elas.momentum.highlight.domain;

import org.elas.momentum.highlight.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Highlight domain model — no Spring context, no external deps.
 */
class HighlightTest {

    @Test
    @DisplayName("Highlight.create validates that mediaUrl must not be blank")
    void create_blankMediaUrl_shouldThrow() {
        assertThatThrownBy(() ->
                Highlight.create("user-1", "  ", MediaType.PHOTO, "caption", "football"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mediaUrl");
    }

    @Test
    @DisplayName("Highlight.create with null mediaUrl throws IllegalArgumentException")
    void create_nullMediaUrl_shouldThrow() {
        assertThatThrownBy(() ->
                Highlight.create("user-1", null, MediaType.PHOTO, "caption", "football"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("incrementLikes increases like count by 1")
    void incrementLikes_shouldIncreaseCount() {
        var h = Highlight.create("user-1", "https://cdn.example.com/pic.jpg",
                MediaType.PHOTO, "Great goal!", "football");
        assertThat(h.getLikeCount()).isZero();

        h.incrementLikes();
        assertThat(h.getLikeCount()).isEqualTo(1);

        h.incrementLikes();
        assertThat(h.getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("decrementLikes decreases like count and cannot go below 0")
    void decrementLikes_cannotGoBelowZero() {
        var h = Highlight.create("user-1", "https://cdn.example.com/pic.jpg",
                MediaType.VIDEO, "Nice dribble", "basketball");
        h.incrementLikes();
        h.incrementLikes(); // likeCount = 2

        h.decrementLikes(); // 1
        assertThat(h.getLikeCount()).isEqualTo(1);

        h.decrementLikes(); // 0
        assertThat(h.getLikeCount()).isZero();

        h.decrementLikes(); // stays at 0
        assertThat(h.getLikeCount()).isZero();
    }

    @Test
    @DisplayName("markAsHighlightOfDay sets isHighlightOfDay flag to true")
    void markAsHighlightOfDay_shouldSetFlag() {
        var h = Highlight.create("user-1", "https://cdn.example.com/pic.jpg",
                MediaType.PHOTO, null, "tennis");

        assertThat(h.isHighlightOfDay()).isFalse();
        h.markAsHighlightOfDay();
        assertThat(h.isHighlightOfDay()).isTrue();
    }

    @Test
    @DisplayName("HighlightId.generate produces unique IDs")
    void highlightId_generate_shouldProduceUniqueIds() {
        var id1 = HighlightId.generate();
        var id2 = HighlightId.generate();
        var id3 = HighlightId.generate();

        assertThat(id1).isNotEqualTo(id2);
        assertThat(id2).isNotEqualTo(id3);
        assertThat(id1.value()).isNotBlank();
    }

    @Test
    @DisplayName("Highlight.reconstitute preserves all provided fields")
    void reconstitute_shouldPreserveAllFields() {
        var id = HighlightId.of("fixed-id-123");
        var publishedAt = Instant.parse("2026-01-15T10:00:00Z");

        var h = Highlight.reconstitute(
                id,
                "publisher-42",
                "https://cdn.example.com/video.mp4",
                MediaType.VIDEO,
                "Amazing volley",
                "tennis",
                33.59,
                -7.61,
                42,
                true,
                publishedAt
        );

        assertThat(h.getId()).isEqualTo(id);
        assertThat(h.getPublisherId()).isEqualTo("publisher-42");
        assertThat(h.getMediaUrl()).isEqualTo("https://cdn.example.com/video.mp4");
        assertThat(h.getMediaType()).isEqualTo(MediaType.VIDEO);
        assertThat(h.getCaption()).isEqualTo("Amazing volley");
        assertThat(h.getSport()).isEqualTo("tennis");
        assertThat(h.getLatitude()).isEqualTo(33.59);
        assertThat(h.getLongitude()).isEqualTo(-7.61);
        assertThat(h.getLikeCount()).isEqualTo(42);
        assertThat(h.isHighlightOfDay()).isTrue();
        assertThat(h.getPublishedAt()).isEqualTo(publishedAt);
    }

    @Test
    @DisplayName("MediaType enum has exactly PHOTO and VIDEO values")
    void mediaType_shouldHavePhotoAndVideo() {
        var values = MediaType.values();
        assertThat(values).containsExactlyInAnyOrder(MediaType.PHOTO, MediaType.VIDEO);
    }
}
