package org.elas.momentum.activity.infrastructure.persistence;

import org.elas.momentum.activity.domain.model.ActivityMessage;
import org.elas.momentum.activity.domain.port.out.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class MessagePersistenceAdapter implements MessageRepository {

    private final ActivityMessageJpaRepository jpaRepository;

    MessagePersistenceAdapter(ActivityMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ActivityMessage save(ActivityMessage message) {
        var entity = new ActivityMessageEntity();
        entity.setId(message.id());
        entity.setActivityId(message.activityId());
        entity.setSenderId(message.senderId());
        entity.setContent(message.content());
        entity.setSentAt(message.sentAt());
        jpaRepository.save(entity);
        return message;
    }

    @Override
    public List<ActivityMessage> findByActivityId(String activityId, int limit) {
        return jpaRepository
                .findByActivityIdOrderBySentAtAsc(activityId, PageRequest.of(0, limit))
                .stream()
                .map(e -> new ActivityMessage(e.getId(), e.getActivityId(),
                        e.getSenderId(), e.getContent(), e.getSentAt()))
                .toList();
    }
}