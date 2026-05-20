package org.elas.momentum.config;

public interface TokenIssuer {
    String generateToken(String userId, String email, String role);
}