package org.elas.momentum.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Vérifie que les règles d'encapsulation Spring Modulith sont respectées.
 * Aucun module ne doit dépendre d'un package interne d'un autre module.
 */
class ModuleStructureTest {

    @Test
    void modulith_structureIsValid() {
        var modules = ApplicationModules.of("org.elas.momentum");
        modules.verify();
    }

    @Test
    void modulith_generateDocumentation() {
        var modules = ApplicationModules.of("org.elas.momentum");
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
