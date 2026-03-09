package org.elas.momentum.messaging.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.messaging.application.dto.ConversationResult;
import org.elas.momentum.messaging.application.dto.MessageResult;
import org.elas.momentum.messaging.application.dto.SendMessageCommand;
import org.elas.momentum.messaging.domain.port.in.GetConversationUseCase;
import org.elas.momentum.messaging.domain.port.in.SendMessageUseCase;
import org.elas.momentum.shared.web.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messaging", description = "Messagerie 1-1")
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;
    private final GetConversationUseCase getConversationUseCase;

    public MessageController(SendMessageUseCase sendMessageUseCase,
                             GetConversationUseCase getConversationUseCase) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.getConversationUseCase = getConversationUseCase;
    }

    @PostMapping
    @Operation(summary = "Envoyer un message à un utilisateur")
    public ResponseEntity<ApiResponse<MessageResult>> send(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SendMessageCommand command) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sendMessageUseCase.send(userId, command)));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Mes conversations")
    public ResponseEntity<ApiResponse<List<ConversationResult>>> getMyConversations(
            @AuthenticationPrincipal String userId) {

        return ResponseEntity.ok(ApiResponse.ok(
                getConversationUseCase.getMyConversations(userId)));
    }

    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Messages d'une conversation")
    public ResponseEntity<ApiResponse<List<MessageResult>>> getMessages(
            @PathVariable String conversationId,
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.ok(
                getConversationUseCase.getMessages(conversationId, userId, page, size)));
    }

    @PostMapping("/conversations/with/{otherUserId}")
    @Operation(summary = "Obtenir ou créer une conversation avec un utilisateur")
    public ResponseEntity<ApiResponse<ConversationResult>> getOrCreateConversation(
            @AuthenticationPrincipal String userId,
            @PathVariable String otherUserId) {

        return ResponseEntity.ok(ApiResponse.ok(
                getConversationUseCase.getOrCreateConversation(userId, otherUserId)));
    }
}
