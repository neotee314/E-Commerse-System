package thkoeln.archilab.ecommerce.solution.thing.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.solution.thing.domain.ThingRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThingService {

    private final ThingRepository thingRepository;
    private final InventoryServiceInterface inventoryServiceInterface;


    public void create(UUID goodId, String name, String description, Float size,
                       Money purchasePrice, Money salesPrice) {
        Thing thing = new Thing(goodId, name, description, size, purchasePrice, salesPrice);
        thingRepository.save(thing);
    }


    public void remove(Thing thing) {
        if (thing == null) throw new ShopException("thing cannot be null");
        thingRepository.delete(thing);
    }


    public Float getSellingPrice(Thing thing) {
        if (thing == null) throw new ShopException("thing cannot be null");
        return thing.getSalesPrice().getAmount();
    }


    public Thing findById(UUID thingId) {
        if (thingId == null) throw new ShopException("thingid cannot be null");
        return thingRepository.findById(thingId).orElse(null);
    }

    public void deleteAllThing() {
        inventoryServiceInterface.deleteAll();
        thingRepository.deleteAll();
    }

}

