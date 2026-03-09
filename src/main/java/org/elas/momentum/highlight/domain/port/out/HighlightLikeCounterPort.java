package org.elas.momentum.highlight.domain.port.out;

public interface HighlightLikeCounterPort {

    void increment(String highlightId);

    void decrement(String highlightId);

    long getCount(String highlightId);

    void syncToDatabase(String highlightId, long count);
}
