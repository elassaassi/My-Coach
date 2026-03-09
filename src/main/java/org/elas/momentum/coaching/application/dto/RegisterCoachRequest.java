package org.elas.momentum.coaching.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegisterCoachRequest(
        @Size(max = 1000)
        String bio,

        @NotNull
        @DecimalMin(value = "0.01", message = "Hourly rate must be greater than 0")
        BigDecimal hourlyRate,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency
) {}
