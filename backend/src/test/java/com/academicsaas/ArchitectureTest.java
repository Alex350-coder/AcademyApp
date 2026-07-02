package com.academicsaas;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

class ArchitectureTest {

    @Test
    void domainLayersShouldNotDependOnInfrastructureOrPresentation() {
        var importedClasses = new ClassFileImporter()
            .importPackages("com.academicsaas");

        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure..", "..presentation..")
            .because("Domain layer must not depend on infrastructure or presentation")
            .check(importedClasses);
    }

    @Test
    void applicationShouldNotDependOnInfrastructureOrPresentation() {
        var importedClasses = new ClassFileImporter()
            .importPackages("com.academicsaas");

        noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure..", "..presentation..")
            .because("Application layer must not depend on infrastructure or presentation")
            .check(importedClasses);
    }

    @Test
    void boundedContextsShouldNotDependOnEachOthersInternalPackages() {
        var importedClasses = new ClassFileImporter()
            .importPackages("com.academicsaas");

        noClasses()
            .that().resideInAPackage("..identity..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..academic..", "..communications..", "..reporting..")
            .because("Bounded contexts must not depend on each other's internal packages")
            .check(importedClasses);
    }

    @Test
    void layeredArchitectureShouldBeRespected() {
        var importedClasses = new ClassFileImporter()
            .importPackages("com.academicsaas");

        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Presentation").definedBy("..presentation..")
            .layer("Shared").definedBy("..shared..")

            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation", "Shared")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Presentation", "Shared")
            .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Infrastructure", "Presentation", "Shared")
            .whereLayer("Presentation").mayOnlyBeAccessedByLayers("Infrastructure", "Shared")
            .whereLayer("Shared").mayOnlyBeAccessedByLayers("Domain", "Application", "Infrastructure", "Presentation", "Shared");

        rule.check(importedClasses);
    }
}
