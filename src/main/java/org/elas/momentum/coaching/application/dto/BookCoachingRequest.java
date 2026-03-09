package org.elas.momentum.coaching.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record BookCoachingRequest(
        @NotBlank
        String offerId,

        @NotBlank
        String clientType,

        @NotNull
        @Future
        Instant scheduledAt
) {}
