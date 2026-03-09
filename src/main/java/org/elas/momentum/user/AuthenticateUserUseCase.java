package org.elas.momentum.user;

/**
 * Port public d'authentification — accessible depuis le module config.
 */
public interface AuthenticateUserUseCase {

    AuthResult authenticate(String email, String rawPassword);

    record AuthResult(String userId, String email, String role) {}
}
