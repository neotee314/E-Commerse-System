package thkoeln.archilab.ecommerce.solution.thing.application;

import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.UUID;


public interface ReservationServiceInterface {
    void removeFromReservedQuantity(Thing thing, int quantity);
    boolean isReserved(Thing thing);
    int getTotalReservedInAllBaskets(Thing thing);
    void deleteAllShoppingBasketParts();
    int getReservedQuantity(UUID shoppingBasketId, Thing thing);
}
