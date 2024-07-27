package thkoeln.archilab.ecommerce.solution.thing.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.UUID;

;

@Service
@RequiredArgsConstructor
public class ThingCatalogService implements ThingCatalogUseCases {

    private final ThingService thingService;

    private final ReservationServiceInterface reservationServiceInterface;

    private final OrderedThingServiceInterface orderedThingServiceInterface;

    private final InventoryServiceInterface inventoryServiceInterface;


    @Override
    public void addThingToCatalog(UUID thingId, String name, String description, Float size,
                                  MoneyType purchasePrice, MoneyType salesPrice) {
        if (thingId == null)
            throw new ShopException("thingId cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing != null) throw new ShopException("Good with id " + thingId + " already exists");

        if (name == null || name.trim().equals("")) {
            throw new ShopException("Name must not be null or empty");
        }

        if (description == null || description.trim().equals("")) {
            throw new ShopException("Description must not be null or empty");
        }

        if (size != null && size <= 0) {
            throw new ShopException("Size must be null or greater than 0");
        }

        if (purchasePrice == null || purchasePrice.getAmount() <= 0) {
            throw new ShopException("Purchase price must not be null or negative");
        }

        if (salesPrice == null || salesPrice.getAmount() <= 0) {
            throw new ShopException("sales price must not be null or negative");
        }

        if (purchasePrice.largerThan(salesPrice)) {
            throw new ShopException("salesprice must not be lower than purchase price");
        }
        thingService.create(thingId, name, description, size, (Money) purchasePrice, (Money) salesPrice);
    }

    @Override
    public void removeThingFromCatalog(UUID thingId) {
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        if (inventoryServiceInterface.isInInventory(thing))
            throw new ShopException("Thing  still has inventory");
        if (reservationServiceInterface.isReserved(thing))
            throw new ShopException("Thing  is still reserved in a shopping basket");
        if (orderedThingServiceInterface.isPartOfCompletedOrder(thing))
            throw new ShopException("Thing  is part of a completed order");
        thingService.remove(thing);
    }

    @Override
    public MoneyType getSalesPrice(UUID thingId) {
        if (thingId == null) throw new ShopException("thignId cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("thing is null");
        return Money.of(thingService.getSellingPrice(thing), "EUR");
    }

    @Override
    public void deleteThingCatalog() {
        reservationServiceInterface.deleteOrderParts();
        reservationServiceInterface.deleteAllShoppingBasketParts();
        thingService.deleteAllThing();
    }
}
