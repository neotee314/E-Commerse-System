package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.archilab.ecommerce.solution.storageunit.application.StorageUnitService;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.test.ClientTestHelper;
import thkoeln.archilab.ecommerce.test.ThingAndStockTestHelper;
import thkoeln.archilab.ecommerce.usecases.*;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.masterdata.Purgatory;


import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static thkoeln.archilab.ecommerce.test.ClientTestHelper.CLIENT_EMAIL;
import static thkoeln.archilab.ecommerce.test.ThingAndStockTestHelper.STORAGE_UNIT_ID;
import static thkoeln.archilab.ecommerce.test.ThingAndStockTestHelper.THING_DATA;

/**
 * This test checks the management of stock across storage units. It focuses on the
 * cases where more than one storage unit are needed to serve the shopping basket of a client.
 */
@SpringBootTest
public class MultipleDeliverysTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private DeliveryPackageUseCases deliveryPackageUseCases;
    @Autowired
    private Purgatory purgatory;

    private ClientTestHelper clientTestHelper;
    private ThingAndStockTestHelper thingAndStockTestHelper;

    private Map<UUID, Integer>  map8_11_14_quantity_2_2_2, map8_11_14_quantity_3_3_4,
                                map10_12_quantity_1_1, map11_quantity_1, map12_quantity_10,
                                map8_9_10_11_quantity_2_1_4_2;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientTestHelper = new ClientTestHelper( clientRegistrationUseCases );
        clientTestHelper.registerAllClients();

        thingAndStockTestHelper = new ThingAndStockTestHelper(
                thingCatalogUseCases, storageUnitUseCases );
        thingAndStockTestHelper.addAllThings();
        thingAndStockTestHelper.addAllStorageUnits();
        thingAndStockTestHelper.addAllStock();

        map8_11_14_quantity_2_2_2 = new HashMap<>() {{
            put( (UUID) THING_DATA[8][0], 2 );
            put( (UUID) THING_DATA[11][0], 2 );
            put( (UUID) THING_DATA[14][0], 2 );
        }};
        map8_11_14_quantity_3_3_4 = new HashMap<>() {{
            put( (UUID) THING_DATA[8][0], 3 );
            put( (UUID) THING_DATA[11][0], 3 );
            put( (UUID) THING_DATA[14][0], 4 );
        }};
        map10_12_quantity_1_1 = new HashMap<>() {{
            put( (UUID) THING_DATA[10][0], 1 );
            put( (UUID) THING_DATA[12][0], 1 );
        }};
        map11_quantity_1 = new HashMap<>() {{
            put( (UUID) THING_DATA[11][0], 1 );
        }};
        map12_quantity_10 = new HashMap<>() {{
            put( (UUID) THING_DATA[12][0], 10 );
        }};
        map8_9_10_11_quantity_2_1_4_2 = new HashMap<>() {{
            put( (UUID) THING_DATA[8][0], 2 );
            put( (UUID) THING_DATA[9][0], 1 );
            put( (UUID) THING_DATA[10][0], 4 );
            put( (UUID) THING_DATA[11][0], 2 );
        }};
    }


    /**
     * Hint: to better understand the expected results of this test, draw a matrix for the
     * distribution of stock across storage units, as a matrix of storage units vs. things.
     * See ![storage units vs. things](./images/StorageUnitVsThing.jpg)
     * (The rightmost numbers will be randomized in the test.)
     */
    @Autowired
    private StorageUnitService storageUnitService;
    @Autowired
    private ThingService thingService;
    @Test
    public void testClosestSingleDeliveryPackagesWins() {
        // given
        EmailType clientEmail3 = CLIENT_EMAIL[3];

        List<StorageUnit> storageUnitss = storageUnitService.findAll();

        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, (UUID) THING_DATA[8][0], 2 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, (UUID) THING_DATA[11][0], 2 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, (UUID) THING_DATA[14][0], 2 );
        // that basket could have been served from storage unit 4 or 7, but 4 is closer to the client
        UUID orderId = shoppingBasketUseCases.checkout( clientEmail3 );


        // when
        List<UUID> storageUnits = deliveryPackageUseCases
                .getContributingStorageUnitsForOrder( orderId );
        Map<UUID, Integer> deliveryPackage4 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[4] );

        // then
        assertEquals( 1, storageUnits.size() );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[4] ) );
        assertEquals( map8_11_14_quantity_2_2_2, deliveryPackage4 );
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testStorageUnitWithEnoughCapacityWins() {
        // given
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, (UUID) THING_DATA[8][0], 3 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, (UUID) THING_DATA[11][0], 3 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, (UUID) THING_DATA[14][0], 4 );
        // that basket could have been served from storage unit 4 or 7, 4 is closer to the client,
        // but only 7 has enough capacity
        UUID orderId = shoppingBasketUseCases.checkout( clientEmail3 );




        // when
        List<UUID> storageUnits = deliveryPackageUseCases
                .getContributingStorageUnitsForOrder( orderId );
        Map<UUID, Integer> deliveryPackage7 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[7] );

        // then
        assertEquals( 1, storageUnits.size() );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[7] ) );
        assertEquals( map8_11_14_quantity_3_3_4, deliveryPackage7 );
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testTwoDeliveryPackages() {
        // given
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail6, (UUID) THING_DATA[10][0], 1 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail6, (UUID) THING_DATA[11][0], 1 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail6, (UUID) THING_DATA[12][0], 1 );
        // that basket needs two delivery packages (2 + 1 things) anyway, and there
        // are 2 options for the bigger one: 10+12 from 5, or 10+11 from 7. The first one is
        // closer. The smaller can then be served from 4 (closest), 7, or 8.
        UUID orderId = shoppingBasketUseCases.checkout( clientEmail6 );


        // when
        List<UUID> storageUnits = deliveryPackageUseCases
                .getContributingStorageUnitsForOrder( orderId );
        Map<UUID, Integer> deliveryPackage5 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[5] );
        Map<UUID, Integer> deliveryPackage4 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[4] );

        // then
        assertEquals( 2, storageUnits.size() );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[5] ) );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[4] ) );
        assertEquals( map10_12_quantity_1_1, deliveryPackage5 );
        assertEquals( map11_quantity_1, deliveryPackage4 );
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testTwoBigDeliveryPackages() {
        // given
        EmailType clientEmail2 = CLIENT_EMAIL[2];
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail2, (UUID) THING_DATA[8][0], 2 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail2, (UUID) THING_DATA[9][0], 1 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail2, (UUID) THING_DATA[10][0], 4 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail2, (UUID) THING_DATA[11][0], 2 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail2, (UUID) THING_DATA[12][0], 10 );
        UUID orderId = shoppingBasketUseCases.checkout( clientEmail2 );

        // when
        List<UUID> storageUnits = deliveryPackageUseCases
                .getContributingStorageUnitsForOrder( orderId );
        Map<UUID, Integer> deliveryPackage7 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[7] );
        Map<UUID, Integer> deliveryPackage5 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[5] );

        // then
        assertEquals( 2, storageUnits.size() );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[7] ) );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[5] ) );
        assertEquals( map8_9_10_11_quantity_2_1_4_2, deliveryPackage7 );
        assertEquals( map12_quantity_10, deliveryPackage5 );
    }


    /**
     * Hint: (see comment on the first test)
     */
    @Test
    public void testOnlyOneSolution() {
        // given
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail6, (UUID) THING_DATA[12][0], 10 );
        UUID orderId = shoppingBasketUseCases.checkout( clientEmail6 );

        // when
        List<UUID> storageUnits = deliveryPackageUseCases
                .getContributingStorageUnitsForOrder( orderId );
        Map<UUID, Integer> deliveryPackage5 = deliveryPackageUseCases
                .getDeliveryPackageForOrderAndStorageUnit( orderId, STORAGE_UNIT_ID[5] );

        // then
        assertEquals( 1, storageUnits.size() );
        assertTrue( storageUnits.contains( STORAGE_UNIT_ID[5] ) );
        assertEquals( map12_quantity_10, deliveryPackage5 );
    }



    private void assertEqualsExpectedMap( Map<UUID, Map<UUID, Integer>> expected,
                                          Map<UUID, Map<UUID, Integer>> actual ) {
        assertEquals( expected.size(), actual.size() );
        Set<UUID> expectedstorageUnitIds = expected.keySet();
        for ( UUID expectedstorageUnitId : expectedstorageUnitIds ) {
            assertEquals( expected.get( expectedstorageUnitId ), actual.get( expectedstorageUnitId ) );
        }
    }
}
