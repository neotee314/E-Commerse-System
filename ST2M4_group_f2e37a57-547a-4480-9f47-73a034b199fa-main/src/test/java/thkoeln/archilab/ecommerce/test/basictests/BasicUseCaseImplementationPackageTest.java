package thkoeln.archilab.ecommerce.test.basictests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BasicUseCaseImplementationPackageTest {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Map<String, List<String>> USECASE_INTERFACES = new HashMap<String, List<String>>() {{
        put("ClientRegistrationUseCases", Arrays.asList("client"));
        put("StorageUnitUseCases", Arrays.asList("storageunit", "stock"));
        put("ThingCatalogUseCases", Arrays.asList("thing"));
        put("ShoppingBasketUseCases", Arrays.asList("shoppingbasket"));
        put("OrderUseCases", Arrays.asList("order"));
    }};
    private static final String USECASE_PATH = "thkoeln.archilab.ecommerce.usecases";
    private static final String SOLUTION_PATH = "thkoeln.archilab.ecommerce.solution";

    @Test
    public void testRequiredClassesDeclared() {
        for ( Map.Entry<String, List<String>> entry : USECASE_INTERFACES.entrySet() ) {
            String interfaceName = USECASE_PATH + "." + entry.getKey();
            List<String> correctTopLevelPackages = entry.getValue();
            Class<?> useCaseInterfaceClass = null;
            try {
                useCaseInterfaceClass = Class.forName( interfaceName );
            } catch (ClassNotFoundException e) {
                fail( "Use case interface " + interfaceName + " not found.", e );
                continue;
            }
            String[] beans = applicationContext.getBeanNamesForType( useCaseInterfaceClass );
            assertEquals( 1, beans.length,
                    "There should be exactly 1 service implementation for the interface " +
                            entry.getKey() + ", but found " + beans.length );
            Object bean = applicationContext.getBean( beans[0] );
            boolean isValid = false;
            for ( String correctTopLevelPackage : correctTopLevelPackages ) {
                String expectedPath = SOLUTION_PATH + "." + correctTopLevelPackage;
                if ( bean.getClass().getPackage().getName().startsWith( expectedPath ) ) {
                    isValid = true;
                    break;
                }
            }
            assertTrue( isValid,
                    "Service " + bean.getClass().getSimpleName() + " should be located somewhere below "
                            + SOLUTION_PATH + " and any of " + correctTopLevelPackages );
        }
    }
}
