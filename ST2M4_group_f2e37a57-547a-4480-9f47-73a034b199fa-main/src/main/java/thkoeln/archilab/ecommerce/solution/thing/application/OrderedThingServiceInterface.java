package thkoeln.archilab.ecommerce.solution.thing.application;


import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

public interface OrderedThingServiceInterface {
    boolean isPartOfCompletedOrder(Thing thing);

    void deleteOrderParts();
}
