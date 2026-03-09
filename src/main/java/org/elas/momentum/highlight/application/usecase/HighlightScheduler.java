package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.port.out.HighlightLikeCounterPort;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class HighlightScheduler {

    private static final Logger log = LoggerFactory.getLogger(HighlightScheduler.class);
    private static final int SYNC_LIMIT = 100;

    private final HighlightRepository highlightRepository;
    private final HighlightLikeCounterPort likeCounterPort;

    public HighlightScheduler(HighlightRepository highlightRepository,
                              HighlightLikeCounterPort likeCounterPort) {
        this.highlightRepository = highlightRepository;
        this.likeCounterPort = likeCounterPort;
    }

    /**
     * Syncs Redis like counts to the database every 5 minutes.
     */
    @Scheduled(fixedDelay = 300_000)
    public void syncLikeCounts() {
        log.info("Syncing highlight like counts from Redis to database...");
        var highlights = highlightRepository.findTopByLikesAndRecency(SYNC_LIMIT);
        for (var highlight : highlights) {
            String id = highlight.getId().value();
            try {
                long redisCount = likeCounterPort.getCount(id);
                if (redisCount > 0) {
                    likeCounterPort.syncToDatabase(id, redisCount);
                }
            } catch (Exception e) {
                log.warn("Failed to sync like count for highlight {}: {}", id, e.getMessage());
            }
        }
        log.info("Highlight like count sync completed for {} highlights", highlights.size());
    }
}
