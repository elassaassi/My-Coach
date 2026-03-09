package org.elas.momentum.messaging.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageCommand(
        @NotBlank(message = "Destinataire obligatoire") String recipientId,

        @NotBlank(message = "Contenu du message obligatoire")
        @Size(max = 2000, message = "Message trop long (max 2000 caractères)")
        String content
) {}
