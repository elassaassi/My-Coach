package org.elas.momentum.user;

import java.util.List;
import java.util.Optional;

/**
 * Interface publique du module User.
 * Seul point d'entrée autorisé depuis les autres modules (Spring Modulith).
 */
public interface UserModuleAPI {
    Optional<UserSummary> findById(String userId);
    boolean exists(String userId);

    /**
     *
     * Trouve un utilisateur par email OAuth2, ou en crée un nouveau.
     * Utilisé par le flux Google / Facebook / Apple.
     *
     * @return userId de l'utilisateur trouvé ou créé
     */
    String findOrCreateOAuthUser(String email, String firstName, String lastName);

    /** Retourne tous les utilisateurs actifs pratiquant ce sport, sauf le demandeur. */
    List<UserSummary> findBySport(String sport, String excludeUserId);
}
