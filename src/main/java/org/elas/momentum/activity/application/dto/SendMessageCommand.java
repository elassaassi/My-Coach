package org.elas.momentum.activity.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageCommand(
        @NotBlank @Size(max = 2000) String content
) {}
