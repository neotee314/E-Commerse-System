package thkoeln.archilab.ecommerce.test.basictests;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("layerRules")
@AnalyzeClasses(packages = "thkoeln")
@SuppressWarnings("PMD")
public class BasicNoOutsideSolutionRulesTest {

    @ArchTest
    static final ArchRule noClassesOutsideSolutionAndDomainprimitives =
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer( "Domainprimitives" ).definedBy( "thkoeln.archilab.ecommerce.domainprimitives.." )
                    .layer( "SolutionClasses" ).definedBy( "thkoeln.archilab.ecommerce.solution.." )
                    .layer( "Tests" ).definedBy( "thkoeln.archilab.ecommerce.test.." )

                    .whereLayer( "Domainprimitives" ).mayOnlyBeAccessedByLayers("SolutionClasses", "Tests" )
                    .whereLayer( "SolutionClasses" ).mayOnlyBeAccessedByLayers( "Tests" );
}
