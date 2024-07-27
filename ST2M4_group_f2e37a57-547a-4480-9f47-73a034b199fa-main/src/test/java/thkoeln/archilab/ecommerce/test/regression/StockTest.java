package thkoeln.archilab.ecommerce.test.regression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.Purgatory;
import thkoeln.archilab.ecommerce.usecases.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.*;

/**
 * This test checks the management of stock across storage units.
 */
@SpringBootTest
public class StockTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private Purgatory purgatory;

    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;
    private UUID storageUnitId0; // id of the first storage unit, where all things type 0 - 6 are stored
    private UUID storageUnitId9; // the empty storage unit
    UUID nonExistingId1;
    UUID nonExistingId2;
    
    @BeforeEach
    public void setUp() {
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases );
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();

        storageUnitId0 = STORAGE_UNIT_ID[0];
        storageUnitId9 = STORAGE_UNIT_ID[9];
        nonExistingId1 = UUID.randomUUID();
        nonExistingId2 = UUID.randomUUID();
    }


    @Test
    public void testAddToStock() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();
        UUID thingId5 = (UUID) THING_DATA[5][0];

        // when
        int stock5before = storageUnitUseCases.getAvailableStock( thingId5 );
        assertEquals( THING_STOCK.get( thingId5 )[0], stock5before );
        storageUnitUseCases.addToStock( storageUnitId0, thingId5, 23 );
        int stock5after = storageUnitUseCases.getAvailableStock( thingId5 );
        storageUnitUseCases.addToStock( storageUnitId0, thingId5, 0 );
        int stock5after2 = storageUnitUseCases.getAvailableStock( thingId5 );
        int stock5after3 = storageUnitUseCases.getAvailableStock(
                storageUnitId0, thingId5 );

        // then
        assertEquals( stock5before + 23, stock5after );
        assertEquals( stock5after, stock5after2 );
        assertEquals( stock5after, stock5after3 );
    }


    @Test
    public void testInvalidAddToStock() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();
        UUID thingId2 = (UUID) THING_DATA[2][0];

        // when
        // then
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addToStock( nonExistingId1, nonExistingId2, 12 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addToStock(
                        storageUnitId0, nonExistingId2, 12 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addToStock(
                        nonExistingId1, thingId2, 12 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.addToStock(
                        storageUnitId0, thingId2, -1 ) );
    }


    @Test
    public void testRemoveFromStock() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();
        UUID thingId5 = (UUID) THING_DATA[5][0];
        int stock5before = THING_STOCK.get( thingId5 )[0];
        UUID thingId6 = (UUID) THING_DATA[6][0];
        int stock6before = THING_STOCK.get( thingId6 )[0];
        UUID thingId0 = (UUID) THING_DATA[0][0];

        // when
        storageUnitUseCases.removeFromStock( storageUnitId0, thingId5, 1 );
        int stock5after = storageUnitUseCases.getAvailableStock( thingId5 );
        storageUnitUseCases.removeFromStock( storageUnitId0, thingId0, 0 );
        int stock0after = storageUnitUseCases.getAvailableStock( thingId0 );
        storageUnitUseCases.removeFromStock(
                storageUnitId0, thingId6, stock6before );
        int stock6after = storageUnitUseCases.getAvailableStock( thingId6 );
        int stock6after2 = storageUnitUseCases.getAvailableStock(
                storageUnitId0, thingId6 );
        // then
        assertEquals( stock5before - 1, stock5after );
        assertEquals( 0, stock0after );
        assertEquals( 0, stock6after );
        assertEquals( stock6after, stock6after2 );
    }


    @Test
    public void testInvalidRemoveFromStock() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();
        UUID thingId5 = (UUID) THING_DATA[5][0];
        int stock5before = THING_STOCK.get( thingId5 )[0];
        UUID thingId0 = (UUID) THING_DATA[0][0];

        // when
        // then
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.removeFromStock( nonExistingId1, nonExistingId2, 12 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.removeFromStock(
                        storageUnitId0, nonExistingId2, 12 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.removeFromStock(
                        nonExistingId1, thingId5, 12 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.removeFromStock(
                        storageUnitId0, thingId5, -1 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.removeFromStock(
                        storageUnitId0, thingId5, stock5before + 1 ) );
        assertThrows( ShopException.class,
                () -> storageUnitUseCases.removeFromStock(
                        storageUnitId0, thingId0, 1 ) );
    }


    @Test
    public void testChangeStock() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();
        UUID thingId5 = (UUID) THING_DATA[5][0];

        // when
        storageUnitUseCases.changeStockTo( storageUnitId0, thingId5, 111 );
        int stock10after = storageUnitUseCases.getAvailableStock( thingId5 );

        // then
        assertEquals( 111, stock10after );
    }


    @Test
    public void testInvalidChangeStock() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();
        UUID thingId6 = (UUID) THING_DATA[6][0];

        // when
        // then
        assertThrows( ShopException.class, () -> storageUnitUseCases.changeStockTo(
                nonExistingId1, nonExistingId2, 12 ) );
        assertThrows( ShopException.class, () -> storageUnitUseCases.changeStockTo(
                nonExistingId1, thingId6, 12 ) );
        assertThrows( ShopException.class, () -> storageUnitUseCases.changeStockTo(
                storageUnitId0, nonExistingId2, 12 ) );
        assertThrows( ShopException.class, () -> storageUnitUseCases.changeStockTo(
                storageUnitId0, thingId6, -1 ) );
    }


    @Test
    public void testEmptyStorageUnit() {
        // given
        thingAndStockMasterDataInitializer.addAllStock();

        // when
        int totalStock = 0;
        for ( int i = 0; i < THING_STOCK.size(); i++ ) {
            UUID thingId = (UUID) THING_DATA[i][0];
            totalStock += storageUnitUseCases.getAvailableStock(
                    storageUnitId9, thingId );
        }

        // then
        assertEquals( 0, totalStock );
    }
}
