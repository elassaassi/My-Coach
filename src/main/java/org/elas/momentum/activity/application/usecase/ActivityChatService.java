package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityMessageResult;
import org.elas.momentum.activity.application.dto.SendMessageCommand;
import org.elas.momentum.activity.domain.port.in.GetActivityUseCase;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMessageEntity;
import org.elas.momentum.activity.infrastructure.persistence.ActivityMessageJpaRepository;
import org.elas.momentum.user.UserModuleAPI;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityChatService {

    private final ActivityMessageJpaRepository messageRepo;
    private final GetActivityUseCase           getActivityUseCase;
    private final UserModuleAPI                userModuleAPI;

    public ActivityChatService(ActivityMessageJpaRepository messageRepo,
                               GetActivityUseCase getActivityUseCase,
                               UserModuleAPI userModuleAPI) {
        this.messageRepo        = messageRepo;
        this.getActivityUseCase = getActivityUseCase;
        this.userModuleAPI      = userModuleAPI;
    }

    public ActivityMessageResult send(String activityId, String senderId, SendMessageCommand cmd) {
        var activity = getActivityUseCase.getById(activityId);

        boolean isMember = activity.participants().stream()
                .anyMatch(p -> p.userId().equals(senderId))
                || activity.organizerId().equals(senderId);

        if (!isMember) {
            throw new IllegalStateException("Tu dois participer à cette session pour envoyer un message");
        }

        var msg = new ActivityMessageEntity();
        msg.setId(UUID.randomUUID().toString());
        msg.setActivityId(activityId);
        msg.setSenderId(senderId);
        msg.setContent(cmd.content().trim());
        msg.setSentAt(Instant.now());
        messageRepo.save(msg);

        return toResult(msg);
    }

    public List<ActivityMessageResult> getMessages(String activityId, int limit) {
        return messageRepo
                .findByActivityIdOrderBySentAtAsc(activityId, PageRequest.of(0, limit))
                .stream()
                .map(this::toResult)
                .toList();
    }

    private ActivityMessageResult toResult(ActivityMessageEntity e) {
        var sender = userModuleAPI.findById(e.getSenderId());
        return new ActivityMessageResult(
                e.getId(), e.getActivityId(), e.getSenderId(),
                sender.map(s -> s.firstName()).orElse(null),
                sender.map(s -> s.lastName()).orElse(null),
                e.getContent(), e.getSentAt());
    }
}
