package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.Purgatory;
import thkoeln.archilab.ecommerce.usecases.ShoppingBasketUseCases;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.ClientRegistrationUseCases;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.THING_DATA;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.STORAGE_UNIT_ID;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.InvalidReason.EMPTY;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.InvalidReason.NULL;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;


@SpringBootTest
public class ThingCatalogTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private Purgatory purgatory;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;


    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases );
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
    }


    @Test
    public void testAddThingToCatalog() {
        // given
        // when
        MoneyType salesPrice = thingCatalogUseCases.getSalesPrice( (UUID) THING_DATA[4][0] );

        // then
        assertEquals( THING_DATA[4][5], salesPrice );
    }


    @Test
    public void testAddThingWithInvalidData() {
        // given
        Object[][] invalidThingData = {
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 0, NULL ),     // id
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 1, NULL ),     // name
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 1, EMPTY ),    // name
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 2, NULL ),     // description
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 2, EMPTY ),    // description
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 4, NULL ),     // buyingPrice
            thingAndStockMasterDataInitializer.getThingDataInvalidAtIndex( 5, NULL ),     // salesPrice
        };

        // when
        // then
        for ( Object[] invalidThingDataInstance : invalidThingData ) {
            StringBuffer invalidDataString = new StringBuffer().append( invalidThingDataInstance[0] )
                    .append( ", " ).append( invalidThingDataInstance[1] ).append( ", " ).append( invalidThingDataInstance[2] )
                    .append( ", " ).append( invalidThingDataInstance[3] ).append( ", " ).append( invalidThingDataInstance[4] )
                    .append( ", " ).append( invalidThingDataInstance[5] );
            assertThrows( ShopException.class, () -> thingAndStockMasterDataInitializer.addThingDataToCatalog( invalidThingDataInstance ),
                    "Invalid data: " + invalidDataString.toString() );
        }
    }


    @Test
    public void testRemoveThingFromCatalog() {
        // given
        UUID thingId = (UUID) THING_DATA[4][0];

        // when
        assertDoesNotThrow( () -> thingCatalogUseCases.getSalesPrice( thingId ) );
        thingCatalogUseCases.removeThingFromCatalog( thingId );

        // then
        assertThrows( ShopException.class, () ->
                thingCatalogUseCases.getSalesPrice( thingId ) );
    }



    @Test
    public void testRemoveNonExistentThing() {
        // given
        UUID nonExistentThingId = UUID.randomUUID();

        // when
        // then
        assertThrows( ShopException.class,
                () -> thingCatalogUseCases.removeThingFromCatalog( nonExistentThingId ) );
    }



    @Test
    public void testRemoveThingThatIsInStock() {
        // given
        UUID thingId = (UUID) THING_DATA[4][0];
        UUID storageUnitId0 = STORAGE_UNIT_ID[0];
        storageUnitUseCases.addToStock( storageUnitId0, thingId, 3 );

        // when
        // then
        assertThrows( ShopException.class,
                () -> thingCatalogUseCases.removeThingFromCatalog( thingId ) );
    }


    @Test
    public void testRemoveThingThatIsInShoppingBasketOrOrder() {
        // given
        UUID thingId3 = (UUID) THING_DATA[3][0];
        UUID thingId4 = (UUID) THING_DATA[4][0];
        UUID storageUnitId0 = STORAGE_UNIT_ID[0];
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        EmailType clientEmail4 = CLIENT_EMAIL[4];
        storageUnitUseCases.addToStock( storageUnitId0, thingId3, 3 );
        storageUnitUseCases.addToStock( storageUnitId0, thingId4, 4 );

        // when
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail3, thingId3, 3 );
        shoppingBasketUseCases.addThingToShoppingBasket(
                clientEmail4, thingId4, 4 );
        shoppingBasketUseCases.checkout( clientEmail4 );

        // then
        assertThrows( ShopException.class,
                () -> thingCatalogUseCases.removeThingFromCatalog( thingId3 ) );
        assertThrows( ShopException.class,
                () -> thingCatalogUseCases.removeThingFromCatalog( thingId4 ) );
    }


    @Test
    public void testClearThingCatalog() {
        // given
        // when
        thingCatalogUseCases.deleteThingCatalog();

        // then
        assertThrows( ShopException.class,
                () -> thingCatalogUseCases.getSalesPrice( (UUID) THING_DATA[4][0] ) );
    }

}
