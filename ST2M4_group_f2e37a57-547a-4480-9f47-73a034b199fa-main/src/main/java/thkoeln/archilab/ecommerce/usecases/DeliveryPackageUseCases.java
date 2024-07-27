package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;

import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * This interface contains methods to query delivery packages belonging to orders.
 * We assume that there is an algorithm in place that has calculated the most cost-efficient
 * setup of delivery packages for a given order.
 */
public interface DeliveryPackageUseCases {

    /**
     * Query which storage units are involved in the delivery of a given order.
     * @param orderId
     * @return A list of storage units that are involved in the delivery of the order.
     *         The list will contain at least one storage unit.
     * @throws ShopException if ...
     *     - orderId is null
     *     - the order with that id does not exist
     */
    public List<UUID> getContributingStorageUnitsForOrder( UUID orderId );


    /**
     * Query the content of the delivery packages for a given order from a given storage unit.
     * @param orderId
     * @param storageUnitId
     * @return A map with thing IDs as keys, and the amount of that things contained in the
     *         delivery package as values. If the storage unit is not involved in the
     *         delivery of the order, the map will be empty.
     * @throws ShopException if ...
     *      - orderId is null, or the order with that id does not exist
     *      - storageUnitId is null, or the storage unit with that id does not exist
     */
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(
            UUID orderId, UUID storageUnitId );


    /**
     * Just for testing purposes. Deletes all delivery packages.
     */
    public void deleteAllDeliveryPackages();
}
