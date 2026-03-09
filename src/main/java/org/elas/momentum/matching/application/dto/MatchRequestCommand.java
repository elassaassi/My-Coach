package org.elas.momentum.matching.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MatchRequestCommand(
        @NotBlank(message = "Sport obligatoire") String sport,
        @NotBlank(message = "Niveau obligatoire") String proficiency,

        @Min(value = -90) @Max(value = 90)
        double latitude,

        @Min(value = -180) @Max(value = 180)
        double longitude,

        @Positive(message = "Distance maximale doit être positive")
        int maxDistanceKm
) {}
