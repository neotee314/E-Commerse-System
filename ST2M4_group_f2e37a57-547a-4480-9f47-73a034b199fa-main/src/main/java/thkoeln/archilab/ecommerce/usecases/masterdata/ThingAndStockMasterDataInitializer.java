package thkoeln.archilab.ecommerce.usecases.masterdata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.*;

/**
 * This is a test helper class that initializes and registers things (but without stock)
 * in the system, using the given interface(s).
 */
@Component
@Slf4j
@SuppressWarnings("PMD")
public class ThingAndStockMasterDataInitializer {

    private ThingCatalogUseCases thingCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;
    private static Random random = new Random();

    public static final String EUR = "EUR";

    @Autowired
    public ThingAndStockMasterDataInitializer( ThingCatalogUseCases thingCatalogUseCases,
                                                                StorageUnitUseCases storageUnitUseCases ) {
        this.thingCatalogUseCases = thingCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
    }

    // Contains initialization data for thing instances, and their stock in the storage unit
    // The data is structured as follows:
    // { UUID, name, description, weight, inPrice, outPrice, storage units }
    // where "storage units" is a string with numbers between 0 and 9, representing the storage units
    // where the thing is stored.
    //
    // There is a total of THING_NUMOF things in the data. The index is (for simplicity)
    // noted as a number at the beginning of the thing name, like "0-TCD-34 v2.1" or "1-EFG-56".
    //
    // With regard to where the stock is stored, the following rules apply:
    // - things 0, 1, 2, and 3 have fixed quantities of 0, 10, 20, and 30, respectively. For simplicity,
    //   they are ONLY available in storage unit 0.

    public static final int THING_NUMOF = 15;
    public static final Object[][] THING_DATA = new Object[][]{
            {UUID.randomUUID(), "0-TCD-34 v2.1", "Universelles Verbindungsstück für den einfachen Hausgebrauch bei der Schnellmontage",
                    1.5f, FactoryMethodInvoker.instantiateMoney( 5.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 10.0f, EUR ), "0"},
            {UUID.randomUUID(), "1-EFG-56", "Hochleistungsfähiger Kondensator für elektronische Schaltungen",
                    0.3f, FactoryMethodInvoker.instantiateMoney( 2.5f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 4.0f, EUR ), "0"},
            {UUID.randomUUID(), "2-MNP-89ff", "Langlebiger und robuster Motor für industrielle Anwendungen",
                    7.2f, FactoryMethodInvoker.instantiateMoney( 50.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 80.0f, EUR ), "0"},
            {UUID.randomUUID(), "3-Gh-25", "Kompakter und leichter Akku für mobile Geräte",
                    null, FactoryMethodInvoker.instantiateMoney( 6.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 8.0f, EUR ), "0"},
            {UUID.randomUUID(), "4-MultiBeast2", "Vielseitiger Adapter für verschiedene Steckertypen",
                    null, FactoryMethodInvoker.instantiateMoney( 0.6f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 1.0f, EUR ), "0"},
            {UUID.randomUUID(), "5-ABC-99 v4.2", "Leistungsstarker Prozessor für Computer und Server",
                    1.0f, FactoryMethodInvoker.instantiateMoney( 150.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 250.0f, EUR ), "0"},
            {UUID.randomUUID(), "6-Stuko22", "Ersatzteil Spitze für Präzisionswerkzeug zum Löten und Schrauben",
                    null, FactoryMethodInvoker.instantiateMoney( 0.3f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 0.5f, EUR ), "0"},
            {UUID.randomUUID(), "7-Btt2-Ah67", "Kraftstoffeffiziente und umweltfreundliche Hochleistungsbatterie",
                    6.0f, FactoryMethodInvoker.instantiateMoney( 80.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 120.0f, EUR ), "123"},
            {UUID.randomUUID(), "8-JKL-67", "Wasserdichtes Gehäuse",
                    3.0f, FactoryMethodInvoker.instantiateMoney( 1.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 1.2f, EUR ), "467"},
            {UUID.randomUUID(), "9-MNO-55-33", "Modulares Netzteil für flexible Stromversorgung",
                    5.5f, FactoryMethodInvoker.instantiateMoney( 25.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 45.0f, EUR ), "578"},
            {UUID.randomUUID(), "10-PQR-80", "Effizienter Kühler für verbesserte Wärmeableitung",
                    4.0f, FactoryMethodInvoker.instantiateMoney( 20.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 35.0f, EUR ), "567"},
            {UUID.randomUUID(), "11-STU-11 Ld", "Hochwertiger Grafikchip für leistungsstarke PCs",
                    null, FactoryMethodInvoker.instantiateMoney( 200.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 350.0f, EUR ), "478"},
            {UUID.randomUUID(), "12-VWX-90 FastWupps", "Schnellladegerät für eine Vielzahl von Geräten",
                    null, FactoryMethodInvoker.instantiateMoney( 15.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 25.0f, EUR ), "5"},
            {UUID.randomUUID(), "13-YZZ-22 v1.8", "Leichter und stabiler Rahmen aus Aluminium",
                    3.5f, FactoryMethodInvoker.instantiateMoney( 60.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 100.0f, EUR ), "78"},
            {UUID.randomUUID(), "14-Nosedive", "Klammer zum Verschließen der Nase beim Tauchen",
                    5.0f, FactoryMethodInvoker.instantiateMoney( 2.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 5.0f, EUR ), "457"}
    };


    /**
     * Used for creating invalid input data for the things in the tests.
     */
    public enum InvalidReason {
        NULL, EMPTY;

        public Object getInvalidValue( Object originalValue ) {
            switch (this) {
                case NULL:
                    return null;
                case EMPTY:
                    return "";
                default:
                    return null;
            }
        }
    }

    public void addAllThings() {
        log.info( "Adding all things to the catalog." );
        for ( Object[] thingData : THING_DATA ) {
            addThingDataToCatalog( thingData );
        }
    }

    public void addThingDataToCatalog( Object[] thingData ) {
        log.info( "... adding " + thingData[1] );
        thingCatalogUseCases.addThingToCatalog( (UUID) thingData[0], (String) thingData[1], (String) thingData[2],
                (Float) thingData[3], (MoneyType) thingData[4], (MoneyType) thingData[5] );
    }

    public Object[] getThingDataInvalidAtIndex( int index, InvalidReason reason ) {
        Object[] thingData = THING_DATA[1];
        Object[] thingDataInvalid = new Object[thingData.length];
        System.arraycopy( thingData, 0, thingDataInvalid, 0, thingData.length );
        thingDataInvalid[index] = thingData[index].getClass().cast(
                reason.getInvalidValue( thingData[index] ) );
        return thingDataInvalid;
    }


    // These home addresss are used for the storage units. The storage unit name will equal
    // the zip code of the home address. Their index number will be visible in the house number.
    // The storage units are used as such:
    // - storage unit 0 is holds all things 0 - 6, and is used for all tests where multiple
    //   delivery packages are irrelevant.
    // - storage units 1 - 3 are used for the proximity tests, where you can deliver things 7 to
    //   to a client from the closest storage unit.
    // - storage units 4 - 8 are used for the tests where you need to deliver things 8 - 14 in
    //   the most cost-efficient way, as multiple delivery packages.
    // - storage unit 9 is empty.
    public final static int STORAGE_UNIT_NUMOF = 10;
    public final static HomeAddressType[] STORAGE_UNIT_ADDRESS = new HomeAddressType[]{
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Stapelallee 0", "Potsdam",
                    FactoryMethodInvoker.instantiateZipCode( "14476" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Lagerhausstr. 1", "Viertelstadt",
                    FactoryMethodInvoker.instantiateZipCode( "02345" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Speicherplatz 2", "Viertelstadt",
                    FactoryMethodInvoker.instantiateZipCode( "02313" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Ablageweg 3", "Reichswürgen",
                    FactoryMethodInvoker.instantiateZipCode( "44923" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Paketstellenallee 4", "Düsseldorf",
                    FactoryMethodInvoker.instantiateZipCode( "40588" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Kaputte-Sachen-Straße 5", "Düren",
                    FactoryMethodInvoker.instantiateZipCode( "52355" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Aufbewahrungsweg 6", "Viernheim",
                    FactoryMethodInvoker.instantiateZipCode( "68519" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Paketallee 7", "Baden-Baden",
                    FactoryMethodInvoker.instantiateZipCode( "76532" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Sendenstr. 8", "Senden",
                    FactoryMethodInvoker.instantiateZipCode( "89250" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Schickweg 9", "Hohenroth",
                    FactoryMethodInvoker.instantiateZipCode( "97618" ) )
    };
    public final static UUID[] STORAGE_UNIT_ID = new UUID[STORAGE_UNIT_NUMOF];

    public void addAllStorageUnits() {
        log.info( "Adding all storage units." );
        for ( int i = 0; i < STORAGE_UNIT_NUMOF; i++ ) {
            STORAGE_UNIT_ID[i] = storageUnitUseCases.addNewStorageUnit(
                    STORAGE_UNIT_ADDRESS[i], STORAGE_UNIT_ADDRESS[i].getZipCode().toString() );
        }
    }


    // These data structures contain the stock of the things in the storage units.
    // THING_STOCK is a map thing id -> Integer[STORAGE_UNIT_NUMOF].
    // The Integer[STORAGE_UNIT_NUMOF] contains the stock of the thing in each of
    // the storage units.
    //
    // The following rules apply:
    // - thing 0 is out of stock
    // - thing 1 / 2 / 3 have fixed quantities of 10 / 20 / 30 respectively, all ONLY in storage unit 0
    // - thing 4 / 5 / 6 have a random stock between 30 and 130, also all ONLY in storage unit 0
    //   (these are the things used for tests on how to add and remove stock)
    // - the others have a random stock between 30 and 130, distributed over several
    //   storage units. Here we follow this convention for simplicity:
    //   - Assume that the thing is available in <n> storage units. Then the first <n-1> storage units
    //     in the list (in ascending sequence) contain 3, and all the remaining stock is in the
    //     last storage unit.

    public static final Map<UUID, Integer[]> THING_STOCK = new HashMap<>();

    static {
        // things 0, 1, 2, and 3 have fixed quantities of 0, 10, 20, and 30.
        THING_STOCK.put( (UUID) THING_DATA[0][0],
                getStockDistribution( 0, (String) THING_DATA[0][6] ) );
        THING_STOCK.put( (UUID) THING_DATA[1][0],
                getStockDistribution( 10, (String) THING_DATA[1][6] ) );
        THING_STOCK.put( (UUID) THING_DATA[2][0],
                getStockDistribution( 20, (String) THING_DATA[2][6] ) );
        THING_STOCK.put( (UUID) THING_DATA[3][0],
                getStockDistribution( 30, (String) THING_DATA[3][6] ) );

        // The other things have a random stock between 30 and 130,
        for ( int i = 4; i < THING_NUMOF; i++ ) {
            Integer totalNumber = random.nextInt( 100 ) + 30;
            Integer[] stockInStorageUnits =
                    getStockDistribution( totalNumber, (String) THING_DATA[i][6] );
            THING_STOCK.put( (UUID) THING_DATA[i][0], stockInStorageUnits );
        }
    }


    /**
     * This method creates a random stock distribution for the given thing.
     *
     * @param totalQuantity - the total number of things in the storage units
     * @param zeroToNine  - a string with numbers between 0 and 9, representing the storage units
     * @return an Integer array with the stock distribution for the thing, according to
     * the rules described above.
     */
    private static Integer[] getStockDistribution( Integer totalQuantity, String zeroToNine ) {
        Integer[] stockInStorageUnits = new Integer[STORAGE_UNIT_NUMOF];
        for ( int i = 0; i < STORAGE_UNIT_NUMOF; i++ ) stockInStorageUnits[i] = 0;
        TreeSet<Integer> storageUnitIndices = getStorageUnitIndices( zeroToNine );
        int numOfIndices = storageUnitIndices.size();
        int currentIndexNumber = 0;
        int currentQuantity = totalQuantity;
        for ( Integer storageUnitIndex : storageUnitIndices ) {
            currentIndexNumber++;
            if ( currentIndexNumber < numOfIndices ) {
                stockInStorageUnits[storageUnitIndex] = 3;
                currentQuantity -= 3;
            } else {
                stockInStorageUnits[storageUnitIndex] = currentQuantity;
            }
        }
        return stockInStorageUnits;
    }


    private static TreeSet<Integer> getStorageUnitIndices( String zeroToNine ) {
        TreeSet<Integer> storageUnitIndices = new TreeSet<>();
        for ( int i = 0; i < zeroToNine.length(); i++ ) {
            storageUnitIndices.add( Integer.parseInt( zeroToNine.substring( i, i + 1 ) ) );
        }
        return storageUnitIndices;
    }


    public void addAllStock() {
        log.info( "Adding all stocks to the storage units." );
        for ( Object[] thingData : THING_DATA ) {
            Integer[] stockInStorageUnits =
                    THING_STOCK.get( thingData[0] );
            for ( int iStorageUnit = 0; iStorageUnit < STORAGE_UNIT_NUMOF; iStorageUnit++ ) {
                if ( stockInStorageUnits[iStorageUnit] > 0 )
                    storageUnitUseCases.addToStock(
                            STORAGE_UNIT_ID[iStorageUnit], (UUID) thingData[0],
                            stockInStorageUnits[iStorageUnit] );
            }
        }
    }


    public Integer findStorageUnitIndex( UUID storageUnitId ) {
        for ( int i = 0; i < STORAGE_UNIT_NUMOF; i++ ) {
            if ( STORAGE_UNIT_ID[i].equals( storageUnitId ) ) {
                return i;
            }
        }
        return null;
    }

    /**
     * This method is used to get the total price of a shopping basket, given as a map of thing id -> quantity.
     * @param quantityThingMap
     * @return the total price of the shopping basket, as Float
     */
    public Float getTotalSalesPrice( Map<UUID, Integer> quantityThingMap ) {
        Float totalPrice = 0.0f;
        for ( UUID thingId : quantityThingMap.keySet() ) {
            Integer quantity = quantityThingMap.get( thingId );
            int thingIndex = -1;
            for ( int i = 0; i < THING_NUMOF; i++ ) {
                if ( THING_DATA[i][0].equals( thingId ) ) {
                    thingIndex = i;
                    break;
                }
            }
            totalPrice += ((MoneyType) THING_DATA[thingIndex][5]).getAmount() * quantity;
        }
        return totalPrice;
    }
}
