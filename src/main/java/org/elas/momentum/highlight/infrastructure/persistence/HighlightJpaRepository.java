package org.elas.momentum.highlight.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HighlightJpaRepository extends JpaRepository<HighlightEntity, String> {

    @Query("""
            SELECT h FROM HighlightEntity h
            ORDER BY h.likeCount DESC, h.publishedAt DESC
            LIMIT :limit
            """)
    List<HighlightEntity> findTopByLikesAndRecency(@Param("limit") int limit);
}
