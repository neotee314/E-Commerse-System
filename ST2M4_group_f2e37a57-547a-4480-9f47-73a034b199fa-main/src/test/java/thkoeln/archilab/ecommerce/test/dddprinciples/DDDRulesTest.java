package thkoeln.archilab.ecommerce.test.dddprinciples;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@ArchTag("layerRules")
@AnalyzeClasses(packages = "thkoeln.archilab.ecommerce.solution")
@SuppressWarnings("PMD")
public class DDDRulesTest {

    @ArchTest
    static final ArchRule domainClassesMayOnlyCallOtherDomainClasses =
            noClasses().that().resideInAPackage( "..domain.." )
                    .should().dependOnClassesThat().resideInAPackage( "..application.." ).allowEmptyShould( true );

    @ArchTest
    static final ArchRule entitiesMustResideInADomainPackage =
            classes().that().areAnnotatedWith( Entity.class ).should().resideInAPackage( "..domain.." )
                    .as( "Entities must reside in a package '..domain..'" ).allowEmptyShould( true );

    @ArchTest
    static final ArchRule entitiesMustNotAccessRepositories =
            noClasses().that().areAnnotatedWith( Entity.class )
                    .should().dependOnClassesThat().areAssignableTo( CrudRepository.class ).allowEmptyShould( false );

    @ArchTest
    static final ArchRule repositoriesMustResideInADomainPackage =
            classes().that().areAssignableTo( CrudRepository.class ).should().resideInAPackage( "..domain.." )
                    .as( "Repositories must reside in a package '..domain..'" ).allowEmptyShould( false );

    @ArchTest
    static final ArchRule repositoryNamesMustHaveProperSuffix =
            classes().that().areAssignableTo( CrudRepository.class )
                    .should().haveSimpleNameEndingWith( "Repository" ).allowEmptyShould( false );

    @ArchTest
    static final ArchRule servicesMustResideInAnApplicationPackage =
            classes().that().areAnnotatedWith( Service.class ).should().resideInAPackage( "..application.." )
                    .as( "Application Services must reside in a package '..application..'" ).allowEmptyShould( false );

    @ArchTest
    static final ArchRule serviceNamesMustHaveProperSuffix =
            classes().that().areAnnotatedWith( Service.class )
                    .should().haveSimpleNameEndingWith( "Service" ).allowEmptyShould( false );

    @ArchTest
    static final ArchRule controllersMustResideInAnApplicationPackage =
            classes().that().areAnnotatedWith( RestController.class ).should().resideInAPackage( "..application.." )
                    .as( "Application Services must reside in a package '..application..'" ).allowEmptyShould( false );

    @ArchTest
    static final ArchRule controllersNamesMustHaveProperSuffix =
            classes().that().areAnnotatedWith( RestController.class )
                    .should().haveSimpleNameEndingWith( "Controller" ).allowEmptyShould( false );


}
