package org.elas.momentum.messaging.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface MessageJpaRepository extends JpaRepository<MessageEntity, String> {
    Page<MessageEntity> findByConversationIdOrderBySentAtDesc(String conversationId, Pageable pageable);
}
