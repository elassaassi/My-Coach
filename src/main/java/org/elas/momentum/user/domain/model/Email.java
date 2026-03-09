package org.elas.momentum.user.domain.model;

import org.elas.momentum.user.domain.exception.InvalidEmailException;

import java.util.Objects;

public record Email(String value) {

    private static final String EMAIL_REGEX = "^[\\w.+\\-]+@[\\w\\-]+\\.[\\w.]+$";

    public Email {
        Objects.requireNonNull(value, "Email cannot be null");
        value = value.toLowerCase().trim();
        if (!value.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException(value);
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
