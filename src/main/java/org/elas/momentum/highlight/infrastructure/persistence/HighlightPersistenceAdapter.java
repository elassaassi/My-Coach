package org.elas.momentum.highlight.infrastructure.persistence;

import org.elas.momentum.highlight.domain.model.Highlight;
import org.elas.momentum.highlight.domain.model.HighlightOfDay;
import org.elas.momentum.highlight.domain.port.out.HighlightOfDayRepository;
import org.elas.momentum.highlight.domain.port.out.HighlightRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
class HighlightPersistenceAdapter implements HighlightRepository, HighlightOfDayRepository {

    private final HighlightJpaRepository highlightJpaRepository;
    private final HighlightOfDayJpaRepository highlightOfDayJpaRepository;

    HighlightPersistenceAdapter(HighlightJpaRepository highlightJpaRepository,
                                HighlightOfDayJpaRepository highlightOfDayJpaRepository) {
        this.highlightJpaRepository = highlightJpaRepository;
        this.highlightOfDayJpaRepository = highlightOfDayJpaRepository;
    }

    // ── HighlightRepository ───────────────────────────────────────────────────

    @Override
    public Highlight save(Highlight highlight) {
        return HighlightMapper.toDomain(
                highlightJpaRepository.save(HighlightMapper.toEntity(highlight)));
    }

    @Override
    public Optional<Highlight> findById(String id) {
        return highlightJpaRepository.findById(id).map(HighlightMapper::toDomain);
    }

    @Override
    public List<Highlight> findTopByLikesAndRecency(int limit) {
        return highlightJpaRepository.findTopByLikesAndRecency(limit).stream()
                .map(HighlightMapper::toDomain)
                .toList();
    }

    // ── HighlightOfDayRepository ──────────────────────────────────────────────

    @Override
    public HighlightOfDay save(HighlightOfDay highlightOfDay) {
        return HighlightMapper.toDomain(
                highlightOfDayJpaRepository.save(HighlightMapper.toEntity(highlightOfDay)));
    }

    @Override
    public Optional<HighlightOfDay> findByDate(LocalDate date) {
        return highlightOfDayJpaRepository.findByDate(date).map(HighlightMapper::toDomain);
    }
}
