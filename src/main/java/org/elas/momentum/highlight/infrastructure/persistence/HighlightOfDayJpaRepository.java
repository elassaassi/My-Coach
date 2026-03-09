package org.elas.momentum.highlight.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

interface HighlightOfDayJpaRepository extends JpaRepository<HighlightOfDayEntity, LocalDate> {

    Optional<HighlightOfDayEntity> findByDate(LocalDate date);
}
