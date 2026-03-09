package org.elas.momentum.coaching.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.elas.momentum.coaching.domain.model.TargetAudience;

import java.math.BigDecimal;

public record CreateOfferRequest(
        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        TargetAudience targetAudience,

        @NotBlank
        @Size(max = 100)
        String sport,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        int durationMin,

        @NotNull
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency
) {}
