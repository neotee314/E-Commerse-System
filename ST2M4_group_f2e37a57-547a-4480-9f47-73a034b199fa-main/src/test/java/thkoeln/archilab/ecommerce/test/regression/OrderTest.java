package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.Purgatory;
import thkoeln.archilab.ecommerce.usecases.*;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;


@SpringBootTest
public class OrderTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private OrderUseCases orderUseCases;

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
        thingAndStockMasterDataInitializer.addAllStock();
    }


    @Test
    public void testorderHistory() {
        // given
        UUID thingId1 = (UUID) thingAndStockMasterDataInitializer.THING_DATA[1][0];
        UUID thingId2 = (UUID) thingAndStockMasterDataInitializer.THING_DATA[2][0];
        EmailType clientEmail = CLIENT_EMAIL[7];
        Map<UUID, Integer> orderHistoryBefore = orderUseCases.getOrderHistory( clientEmail );

        // when
        shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId1, 3 );
        shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId2, 2 );
        shoppingBasketUseCases.checkout( clientEmail );
        Map<UUID, Integer> orderHistory1 = orderUseCases.getOrderHistory( clientEmail );
        shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId1, 6 );
        shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId2, 2 );
        shoppingBasketUseCases.checkout( clientEmail );
        Map<UUID, Integer> orderHistory2 = orderUseCases.getOrderHistory( clientEmail );
        shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId1, 1 );
        shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId2, 6 );
        shoppingBasketUseCases.checkout( clientEmail );
        Map<UUID, Integer> orderHistory3 = orderUseCases.getOrderHistory( clientEmail );

        // then
        assertEquals( 0, orderHistoryBefore.size() );
        assertEquals( 2, orderHistory1.size() );
        assertEquals( 2, orderHistory2.size() );
        assertEquals( 2, orderHistory3.size() );
        assertEquals( 3, orderHistory1.get( thingId1 ) );
        assertEquals( 2, orderHistory1.get( thingId2 ) );
        assertEquals( 9, orderHistory2.get( thingId1 ) );
        assertEquals( 4, orderHistory2.get( thingId2 ) );
        assertEquals( 10, orderHistory3.get( thingId1 ) );
        assertEquals( 10, orderHistory3.get( thingId2 ) );
    }


    @Test
    public void testForEmptyOrderHistory() {
        // given
        EmailType clientEmail4 = CLIENT_EMAIL[4];

        // when
        // then
        Map<UUID, Integer> orderHistory = orderUseCases.getOrderHistory(
                clientEmail4 );
        assertEquals( 0, orderHistory.size() );
    }

}
