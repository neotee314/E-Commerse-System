package thkoeln.archilab.ecommerce.solution.thing.application;

import thkoeln.archilab.ecommerce.solution.thing.domain.Reservable;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;


public interface ReservationServiceInterface {
    void removeFromReservedQuantity(Thing thing, int quantity);
    boolean isReserved(Thing thing);
    int getTotalReservedInAllBaskets(Thing thing);
    void deleteAllShoppingBasketParts();
    int getReservedQuantity(Reservable shoppingBasket, Thing thing);
}
