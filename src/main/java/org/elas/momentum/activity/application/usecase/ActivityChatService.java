package org.elas.momentum.activity.application.usecase;

import org.elas.momentum.activity.application.dto.ActivityMessageResult;
import org.elas.momentum.activity.application.dto.SendMessageCommand;
import org.elas.momentum.activity.domain.model.ActivityMessage;
import org.elas.momentum.activity.domain.port.in.GetMessagesUseCase;
import org.elas.momentum.activity.domain.port.in.GetActivityUseCase;
import org.elas.momentum.activity.domain.port.in.SendMessageUseCase;
import org.elas.momentum.activity.domain.port.out.MessageRepository;
import org.elas.momentum.user.UserModuleAPI;
import org.elas.momentum.user.UserSummary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ActivityChatService implements SendMessageUseCase, GetMessagesUseCase {

    private final MessageRepository     messageRepository;
    private final GetActivityUseCase    getActivityUseCase;
    private final UserModuleAPI         userModuleAPI;

    public ActivityChatService(MessageRepository messageRepository,
                               GetActivityUseCase getActivityUseCase,
                               UserModuleAPI userModuleAPI) {
        this.messageRepository  = messageRepository;
        this.getActivityUseCase = getActivityUseCase;
        this.userModuleAPI      = userModuleAPI;
    }

    @Override
    public ActivityMessageResult send(String activityId, String senderId, SendMessageCommand cmd) {
        var activity = getActivityUseCase.getById(activityId);

        boolean isMember = activity.participants().stream().anyMatch(p -> p.userId().equals(senderId))
                || activity.organizerId().equals(senderId);
        if (!isMember) {

            throw new IllegalStateException("Tu dois participer à cette session pour envoyer un message");
        }

        var message = ActivityMessage.create(activityId, senderId, cmd.content());
        messageRepository.save(message);

        var sender = userModuleAPI.findById(senderId);
        return toResult(message, sender.orElse(null));
    }

    @Override
    public List<ActivityMessageResult> getMessages(String activityId, int limit) {
        var messages = messageRepository.findByActivityId(activityId, limit);

        Set<String> senderIds = messages.stream().map(ActivityMessage::senderId).collect(Collectors.toSet());
        Map<String, UserSummary> senders = userModuleAPI.findByIds(senderIds);

        return messages.stream()
                .map(m -> toResult(m, senders.get(m.senderId())))
                .toList();
    }

    private static ActivityMessageResult toResult(ActivityMessage m, UserSummary sender) {
        return new ActivityMessageResult(
                m.id(), m.activityId(), m.senderId(),
                sender != null ? sender.firstName() : null,
                sender != null ? sender.lastName()  : null,
                sender != null ? sender.avatarUrl() : null,
                m.content(), m.sentAt());
    }
}