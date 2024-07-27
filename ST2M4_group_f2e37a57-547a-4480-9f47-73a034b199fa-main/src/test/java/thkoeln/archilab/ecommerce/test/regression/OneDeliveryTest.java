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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.THING_DATA;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.STORAGE_UNIT_ID;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;

/**
 * This test checks the management of stock across storage units. It focuses on the
 * cases where only one storage unit can serve the shopping basket of a client.
 */
@SpringBootTest
public class OneDeliveryTest {
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

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    private Integer[][] closestStorageUnitIndex = new Integer[][]{
            {2},          // client 0 at 02314 => delivery from 02313 (2)
            {1, 2},       // client 1 at 02368 => delivery from 02345 (1) or 02313 (2)
            {1, 2},       // client 2 at 12345 => delivery from 02345 (1) or 02313 (2)
            {3},          // client 3 at 31463 => delivery from 44923 (3)
            {1, 2, 3},    // client 4 at 72162 => delivery from 44923 (3) or 02345 (1) or 02313 (2)
            {1, 2}        // client 5 at 82195 => delivery from from 02345 (1) or 02313 (2)
    };

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


    /**
     * Different clients always buy the same thing (no. 7). The thing is available in
     * storage unit no. {1, 2, 3}. The test checks if the correct (closest) storage unit is
     * selected for each client.
     */
    @Test
    public void testClosestDeliveryPackage() {
        // given
        UUID thingId7 = (UUID) THING_DATA[7][0];
        UUID[] orderIds = new UUID[6];

        // when
        for ( int iClient = 0; iClient <= 5; iClient++ ) {
            EmailType clientEmail = CLIENT_EMAIL[iClient];
            shoppingBasketUseCases.addThingToShoppingBasket( clientEmail, thingId7, 1 );
            orderIds[iClient] = shoppingBasketUseCases.checkout( clientEmail );
        }

        // then
        for ( int iClient = 0; iClient <= 5; iClient++ ) {
            EmailType clientEmail = CLIENT_EMAIL[iClient];
            List<UUID> storageUnits = deliveryPackageUseCases
                    .getContributingStorageUnitsForOrder( orderIds[iClient] );
            assertEquals( 1, storageUnits.size() );
            assertStorageUnitMatches( iClient, clientEmail, storageUnits );

            Map<UUID, Integer> thingQuantity = deliveryPackageUseCases
                    .getDeliveryPackageForOrderAndStorageUnit( orderIds[iClient],
                            storageUnits.get( 0 ) );
            assertEquals( 1, thingQuantity.size() );
            assertEquals( thingId7, thingQuantity.keySet().iterator().next() );
            assertEquals( 1, thingQuantity.values().iterator().next() );
        }
    }


    private void assertStorageUnitMatches( int iClient, EmailType clientEmail,
                                                List<UUID> storageUnits ) {
        Integer[] closestStorageUnitIndices = closestStorageUnitIndex[iClient];
        for ( Integer closestStorageUnitIndex : closestStorageUnitIndices ) {
            if ( storageUnits.contains( STORAGE_UNIT_ID[closestStorageUnitIndex] ) ) {
                return;
            }
        }
        String msg = "Wrong storage unit selected for client " + clientEmail + ".\n" +
                "Expected one of the following storage unit indices: {";
        for ( int i = 0; i < closestStorageUnitIndices.length; i++ ) {
            msg += closestStorageUnitIndices[i];
            if ( i < closestStorageUnitIndices.length - 1 ) msg += ", ";
        }
        msg += "}, but got the following indices: {";
        for ( int i = 0; i < storageUnits.size(); i++ ) {
            UUID id = storageUnits.get( i );
            Integer idIndex = thingAndStockMasterDataInitializer.findStorageUnitIndex( id );
            msg += idIndex;
            if ( i < storageUnits.size() - 1 ) msg += ", ";
        }
        msg += "}";
        fail( msg );
    }

}
