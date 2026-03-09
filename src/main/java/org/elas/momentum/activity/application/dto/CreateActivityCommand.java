package org.elas.momentum.activity.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateActivityCommand(
        @NotBlank(message = "Titre obligatoire") String title,
        String description,
        @NotBlank(message = "Sport obligatoire") String sport,
        String requiredLevel,

        @Min(-90) @Max(90) double latitude,
        @Min(-180) @Max(180) double longitude,
        @NotBlank(message = "Ville obligatoire") String city,
        String venueName,
        String country,

        @NotNull(message = "Date/heure obligatoire")
        @Future(message = "L'activité doit être programmée dans le futur")
        Instant scheduledAt,

        @Positive(message = "Nombre de participants doit être positif")
        @Min(value = 2, message = "Minimum 2 participants")
        int maxParticipants
) {}
