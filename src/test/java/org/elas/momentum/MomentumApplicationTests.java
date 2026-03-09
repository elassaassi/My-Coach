package org.elas.momentum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de démarrage du contexte Spring.
 * Utilise H2 en mémoire — aucune DB externe requise.
 */
@SpringBootTest
@ActiveProfiles("test")
class MomentumApplicationTests {

    @Test
    void contextLoads() {
    }

}
