package org.elas.momentum.user.application.dto;

public record RegisterUserCommand(
        String email,
        String password,
        String firstName,
        String lastName
) {}
