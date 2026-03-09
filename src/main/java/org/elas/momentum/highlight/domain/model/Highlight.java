package org.elas.momentum.highlight.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate Root — Highlight Bounded Context.
 */
public class Highlight {

    private final HighlightId id;
    private final String publisherId;
    private final String mediaUrl;
    private final MediaType mediaType;
    private String caption;
    private String sport;
    private Double latitude;
    private Double longitude;
    private int likeCount;
    private boolean isHighlightOfDay;
    private final Instant publishedAt;

    // ── Factory : création ────────────────────────────────────────────────────

    public static Highlight create(String publisherId, String mediaUrl, MediaType mediaType,
                                   String caption, String sport) {
        Objects.requireNonNull(publisherId, "publisherId must not be null");
        Objects.requireNonNull(mediaType, "mediaType must not be null");
        if (mediaUrl == null || mediaUrl.isBlank()) {
            throw new IllegalArgumentException("mediaUrl must not be blank");
        }
        var h = new Highlight(
                HighlightId.generate(),
                publisherId,
                mediaUrl,
                mediaType,
                caption,
                sport,
                null,
                null,
                0,
                false,
                Instant.now()
        );
        return h;
    }

    // ── Factory : reconstitution depuis persistance ────────────────────────────

    public static Highlight reconstitute(HighlightId id, String publisherId, String mediaUrl,
                                         MediaType mediaType, String caption, String sport,
                                         Double latitude, Double longitude, int likeCount,
                                         boolean isHighlightOfDay, Instant publishedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(publisherId, "publisherId must not be null");
        return new Highlight(id, publisherId, mediaUrl, mediaType, caption, sport,
                latitude, longitude, likeCount, isHighlightOfDay, publishedAt);
    }

    // ── Constructor interne ───────────────────────────────────────────────────

    private Highlight(HighlightId id, String publisherId, String mediaUrl, MediaType mediaType,
                      String caption, String sport, Double latitude, Double longitude,
                      int likeCount, boolean isHighlightOfDay, Instant publishedAt) {
        this.id = id;
        this.publisherId = publisherId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.caption = caption;
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likeCount = likeCount;
        this.isHighlightOfDay = isHighlightOfDay;
        this.publishedAt = publishedAt;
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void incrementLikes() {
        this.likeCount++;
    }

    public void decrementLikes() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void markAsHighlightOfDay() {
        this.isHighlightOfDay = true;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public HighlightId getId() { return id; }
    public String getPublisherId() { return publisherId; }
    public String getMediaUrl() { return mediaUrl; }
    public MediaType getMediaType() { return mediaType; }
    public String getCaption() { return caption; }
    public String getSport() { return sport; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public int getLikeCount() { return likeCount; }
    public boolean isHighlightOfDay() { return isHighlightOfDay; }
    public Instant getPublishedAt() { return publishedAt; }
}
