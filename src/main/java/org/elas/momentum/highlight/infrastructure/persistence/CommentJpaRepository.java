package org.elas.momentum.highlight.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface CommentJpaRepository extends JpaRepository<CommentEntity, String> {

    List<CommentEntity> findByHighlightIdOrderByCreatedAtAsc(String highlightId);

    @Query("SELECT c.highlightId, COUNT(c) FROM CommentEntity c WHERE c.highlightId IN :ids GROUP BY c.highlightId")
    List<Object[]> countByHighlightIds(@Param("ids") List<String> ids);
}
