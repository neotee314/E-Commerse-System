package thkoeln.archilab.ecommerce.solution.storageunit.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.stocklevel.domain.StockLevel;
import thkoeln.archilab.ecommerce.solution.stocklevel.application.StockLevelService;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnitRepository;
import thkoeln.archilab.ecommerce.solution.thing.application.ReservationServiceInterface;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageUnitUseCaseService implements StorageUnitUseCases {

    private final StorageUnitRepository storageUnitRepository;
    private final StorageUnitService storageUnitService;
    private final ThingService thingService;
    private final ReservationServiceInterface reservationServiceInterface;
    private final StockLevelService stockLevelService;

    @Override
    public UUID addNewStorageUnit(HomeAddressType address, String name) {
        if (address == null || name == null || name.isEmpty()) throw new ShopException("indalvid data");
        StorageUnit storageUnit = new StorageUnit(address, name);
        storageUnitRepository.save(storageUnit);
        return storageUnit.getStorageId();
    }

    @Override
    public void deleteAllStorageUnits() {
        storageUnitRepository.deleteAll();
    }

    @Override
    public void addToStock(UUID storageUnitId, UUID thingId, int addedQuantity) {
        if (thingId == null || storageUnitId == null)
            throw new ShopException("thingId cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("thing does not exist");
        if (addedQuantity < 0)
            throw new ShopException("the added quantity cannot be negative");
        StorageUnit storageUnit = storageUnitService.findById(storageUnitId);
        StockLevel stockLevel = new StockLevel(thing, addedQuantity);
        storageUnit.addToStock(stockLevel);
        stockLevelService.save(stockLevel);
        storageUnitRepository.save(storageUnit);
    }

    @Override
    public void removeFromStock(UUID storageUnitId, UUID thingId, int removedQuantity) {
        if (thingId == null || storageUnitId == null) throw new ShopException("thingId cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null || removedQuantity < 0) throw new ShopException("thing is null or quantity negative");
        StockLevel stockLevel = new StockLevel(thing, removedQuantity);
        StorageUnit storageUnit = storageUnitService.findById(storageUnitId);
        if (storageUnit == null) throw new ShopException("invalid storage");

        int currentInventory = storageUnit.getAvailableStock(thing);
        int currentlyReserved = reservationServiceInterface.getTotalReservedInAllBaskets(thing);
        if (removedQuantity > currentInventory + currentlyReserved)
            throw new ShopException("The removed quantity is greater than the available quantity");

        int stockAfter = currentInventory - removedQuantity;
        if (stockAfter >= 0) {
            storageUnit.removeFromStock(stockLevel);
            storageUnitRepository.save(storageUnit);
            return;
        }
        if (currentInventory > 0) storageUnit.removeFromStock(new StockLevel(thing, currentInventory));
        storageUnitRepository.save(storageUnit);
        reservationServiceInterface.removeFromReservedQuantity(thing, removedQuantity - currentInventory);

    }

    @Override
    public void changeStockTo(UUID storageUnitId, UUID thingId, int newTotalQuantity) {
        if (thingId == null || storageUnitId == null)
            throw new ShopException("thingid cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null || newTotalQuantity < 0)
            throw new ShopException("thing is null or quantity negative");

        int reservedQuantity = reservationServiceInterface.getTotalReservedInAllBaskets(thing);

        if (newTotalQuantity < reservedQuantity) {
            int lostQuantity = reservedQuantity - newTotalQuantity;
            reservationServiceInterface.removeFromReservedQuantity(thing, lostQuantity);
            StorageUnit storageUnit = storageUnitService.findById(storageUnitId);
            StockLevel stockLevel = new StockLevel(thing, 0);
            storageUnit.changeStockTo(stockLevel);
            storageUnitRepository.save(storageUnit);
            return;
        }
        StorageUnit storageUnit = storageUnitService.findById(storageUnitId);
        StockLevel stockLevel = new StockLevel(thing, newTotalQuantity);
        storageUnit.changeStockTo(stockLevel);
        storageUnitRepository.save(storageUnit);

    }

    @Override
    public int getAvailableStock(UUID storageUnitId, UUID thingId) {
        if (storageUnitId == null || thingId == null) throw new ShopException("invalid data");
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("invalid data");
        StorageUnit storageUnit = storageUnitService.findById(storageUnitId);
        return storageUnit.getAvailableStock(thing);
    }

    @Override
    public int getAvailableStock(UUID thingId) {
        if (thingId == null) {
            throw new ShopException("thingId cannot be null");
        }
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("thing cannot be null");
        List<StorageUnit> storageUnitList = storageUnitService.findAll();
        int availableStock = 0;
        for (StorageUnit storageUnit : storageUnitList) {
            availableStock += storageUnit.getAvailableStock(thing);
        }
        return availableStock;
    }

}
