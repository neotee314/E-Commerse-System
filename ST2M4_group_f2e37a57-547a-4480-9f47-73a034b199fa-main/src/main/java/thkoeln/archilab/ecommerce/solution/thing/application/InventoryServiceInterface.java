package thkoeln.archilab.ecommerce.solution.thing.application;

import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

public interface InventoryServiceInterface {
    boolean isInInventory(Thing thing);
    void deleteAll();
}
