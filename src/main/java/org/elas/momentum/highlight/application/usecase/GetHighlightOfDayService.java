package org.elas.momentum.highlight.application.usecase;

import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.HighlightOfDay;
import org.elas.momentum.highlight.domain.port.in.GetHighlightOfDayUseCase;
import org.elas.momentum.highlight.domain.port.out.HighlightOfDayRepository;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetHighlightOfDayService implements GetHighlightOfDayUseCase {

    private static final int TOP_CANDIDATES = 20;

    private final HighlightOfDayRepository highlightOfDayRepository;
    private final HighlightRepository highlightRepository;

    public GetHighlightOfDayService(HighlightOfDayRepository highlightOfDayRepository,
                                    HighlightRepository highlightRepository) {
        this.highlightOfDayRepository = highlightOfDayRepository;
        this.highlightRepository = highlightRepository;
    }

    @Override
    @Transactional
    public HighlightOfDay getToday() {
        LocalDate today = LocalDate.now();
        return highlightOfDayRepository.findByDate(today)
                .orElseGet(() -> selectHighlightOfDay(today));
    }

    @Override
    public Optional<Highlight> getTodayHighlight() {
        LocalDate today = LocalDate.now();
        return highlightOfDayRepository.findByDate(today)
                .flatMap(hod -> highlightRepository.findById(hod.highlightId()));
    }

    @Transactional
    private HighlightOfDay selectHighlightOfDay(LocalDate date) {
        List<Highlight> candidates = highlightRepository.findTopByLikesAndRecency(TOP_CANDIDATES);
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No highlights available to select highlight of the day");
        }

        // Score = likes * 0.7 + recency * 0.3
        // Recency is normalised: more recent = higher rank index (reversed)
        long now = System.currentTimeMillis();
        Highlight best = candidates.stream()
                .max(Comparator.comparingDouble(h -> computeScore(h, now, candidates.size())))
                .orElseThrow();

        best.markAsHighlightOfDay();
        highlightRepository.save(best);

        HighlightOfDay hod = HighlightOfDay.select(date, best.getId().value());
        return highlightOfDayRepository.save(hod);
    }

    private double computeScore(Highlight h, long nowMs, int total) {
        double likeScore = h.getLikeCount();
        // Recency: older publishedAt = smaller value; invert to get recency score
        long ageMs = nowMs - h.getPublishedAt().toEpochMilli();
        // Normalise age: assume max 7 days window
        double maxAgeMs = 7L * 24 * 3600 * 1000;
        double recencyScore = Math.max(0, 1.0 - (ageMs / maxAgeMs)) * 100;
        return likeScore * 0.7 + recencyScore * 0.3;
    }
}
