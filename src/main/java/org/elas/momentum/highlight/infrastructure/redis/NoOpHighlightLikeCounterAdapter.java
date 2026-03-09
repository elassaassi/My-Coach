package org.elas.momentum.highlight.infrastructure.redis;

import org.elas.momentum.highlight.domain.port.out.HighlightLikeCounterPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * No-op implementation used in test profile (no Redis required).
 */
@Component
@Profile("test")
class NoOpHighlightLikeCounterAdapter implements HighlightLikeCounterPort {

    @Override
    public void increment(String highlightId) {}

    @Override
    public void decrement(String highlightId) {}

    @Override
    public long getCount(String highlightId) { return 0L; }

    @Override
    public void syncToDatabase(String highlightId, long count) {}
}
