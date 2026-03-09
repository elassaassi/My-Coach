package org.elas.momentum.messaging.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface ConversationJpaRepository extends JpaRepository<ConversationEntity, String> {

    @Query("""
            SELECT c FROM ConversationEntity c
            WHERE (c.participantA = :a AND c.participantB = :b)
               OR (c.participantA = :b AND c.participantB = :a)
            """)
    Optional<ConversationEntity> findByParticipants(@Param("a") String a, @Param("b") String b);

    @Query("""
            SELECT c FROM ConversationEntity c
            WHERE c.participantA = :userId OR c.participantB = :userId
            ORDER BY c.lastMessageAt DESC NULLS LAST
            """)
    List<ConversationEntity> findByParticipant(@Param("userId") String userId);
}
