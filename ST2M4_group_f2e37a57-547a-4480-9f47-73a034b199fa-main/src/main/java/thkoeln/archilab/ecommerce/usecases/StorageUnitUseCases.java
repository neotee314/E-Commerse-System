package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.Map;
import java.util.UUID;


/**
 * This interface contains methods needed in the context of handling the shop stock,
 * i.e. adding and removing things in the storage unit.
 */
public interface StorageUnitUseCases {
    /**
     * Adds a new storage unit to the shop
     * @param address
     * @param name
     * @return the id of the new storage unit
     * @throws ShopException if ...
     *      - address is null
     *      - name is null or empty
     */
    public UUID addNewStorageUnit( HomeAddressType address, String name );


    /**
     * Deletes all storage units from the shop. Intended for testing purposes.
     * @throws ShopException if the things catalog is not empty
     */
    public void deleteAllStorageUnits();


    /**
     * Adds a certain quantity of a given thing to the stock
     * @param storageUnitId
     * @param thingId
     * @param addedQuantity
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - thingId is null
     *      - the thing with that id does not exist
     *      - addedQuantity <= 0
     */
    public void addToStock( UUID storageUnitId, UUID thingId, int addedQuantity );


    /**
     * Removes a certain quantity of a given thing from the stock.
     * If the new total quantity is lower than the currently reserved things, some of currently reserved things
     * (in the clients' shopping baskets) are removed. This means that some of the reserved things are lost for
     * the client. (This is necessary because there probably was a mistake in the stock management, a mis-counting,
     * or some of the things were stolen from the storage unit, are broken, etc.)
     * @param storageUnitId
     * @param thingId
     * @param removedQuantity
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - thingId is null
     *      - the thing with that id does not exist
     *      - removedQuantity < 0
     *      - the removed quantity is greater than the current stock and the currently reserved things together
     */
    public void removeFromStock( UUID storageUnitId, UUID thingId, int removedQuantity );


    /**
     * Changes the total quantity of a given thing in the stock.
     * If the new total quantity is lower than the currently reserved things, some of currently reserved things
     * (in the clients' shopping baskets) are removed. This means that some of the reserved things are lost for
     * the client. (This is necessary because there probably was a mistake in the stock management, a mis-counting,
     * or some of the things were stolen from the storage unit, are broken, etc.)
     * @param storageUnitId
     * @param thingId
     * @param newTotalQuantity
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - thingId is null
     *      - the thing with that id does not exist
     *      - newTotalQuantity < 0
     */
    public void changeStockTo( UUID storageUnitId, UUID thingId, int newTotalQuantity );


    /**
     * Get the current stock of a given thing in one specific storage unit.
     * @param storageUnitId
     * @param thingId
     * @return the current total stock of the thing
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - thingId is null
     *      - the thing with that id does not exist
     */
    public int getAvailableStock( UUID storageUnitId, UUID thingId );


    /**
     * Get the current total stock of a given thing, across all storage units, and including the currently
     * reserved things in shopping baskets.
     * @param thingId
     * @return the current total stock of the thing
     * @throws ShopException if ...
     *      - thingId is null
     *      - the thing with that id does not exist
     */
    public int getAvailableStock( UUID thingId );
}
