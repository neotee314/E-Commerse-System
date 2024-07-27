package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker;
import thkoeln.archilab.ecommerce.usecases.masterdata.Purgatory;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.THING_DATA;


/**
 * This test checks the management and lifecycle of storage units.
 */
@SpringBootTest
public class StorageUnitManagementTest {
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;

    @Autowired
    private Purgatory purgatory;

    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    private HomeAddressType homeAddress1, homeAddress2;

    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases );
        thingAndStockMasterDataInitializer.addAllThings();

        homeAddress1 = FactoryMethodInvoker.instantiateHomeAddress(
                "Hauptstraße 1", "Berlin",
                FactoryMethodInvoker.instantiateZipCode( "10115" ) );
        homeAddress2 = FactoryMethodInvoker.instantiateHomeAddress(
                "Kirchplatz 2", "Hamburg",
                FactoryMethodInvoker.instantiateZipCode( "20095" ) );
    }

    @Test
    public void testAddInvalidStorageUnit() {
        // given
        // when
        // then
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addNewStorageUnit( null, null ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addNewStorageUnit( homeAddress1, null ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addNewStorageUnit( homeAddress1, "" ) );
    }


    @Test
    public void testDeleteAllStorageUnits() {
        // given
        UUID id1 = storageUnitUseCases.addNewStorageUnit( homeAddress1, "Lager1" );
        UUID id2 = storageUnitUseCases.addNewStorageUnit( homeAddress2, "Lager2" );
        UUID thingId9 = (UUID) THING_DATA[9][0];

        // when
        int numOf1 = storageUnitUseCases.getAvailableStock( thingId9 );
        int numOf2 = storageUnitUseCases.getAvailableStock( thingId9 );

        // then
        assertNotEquals( id1, id2 );
        assertEquals( 0, numOf1 );
        assertEquals( 0, numOf2 );
        storageUnitUseCases.deleteAllStorageUnits();
        // ... ids must be invalid now
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.getAvailableStock( id1 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.getAvailableStock( id2 ) );
    }
}
