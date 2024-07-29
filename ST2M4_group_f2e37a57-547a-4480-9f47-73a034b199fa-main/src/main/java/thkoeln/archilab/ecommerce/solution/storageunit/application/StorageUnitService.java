package thkoeln.archilab.ecommerce.solution.storageunit.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.ZipCode;
import thkoeln.archilab.ecommerce.solution.stocklevel.application.StockLevelService;
import thkoeln.archilab.ecommerce.solution.stocklevel.domain.StockLevel;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnitRepository;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageUnitService {

    private final StorageUnitRepository storageUnitRepository;
    private final StockLevelService stockLevelService;

    public StorageUnit findById(UUID storageUnitId) {
        Optional<StorageUnit> storageUnitOptional = storageUnitRepository.findById(storageUnitId);

        if (storageUnitOptional.isEmpty())
            throw new ShopException("Storage with Id " + storageUnitId + " does not exist");
        return storageUnitOptional.get();

    }

    public List<StorageUnit> findAll() {
        List<StorageUnit> storageUnits = new ArrayList<>();
        storageUnitRepository.findAll().forEach(storageUnits::add);
        return storageUnits;
    }

    public void addToStock(StorageUnit storageUnit, StockLevel stockLevel) {

        StockLevel found = findStockLevel(storageUnit, stockLevel);
        if (found != null) {
            stockLevelService.addToQuantity(found, stockLevel.getQuantity());
            return;
        }

        storageUnit.getStockLevels().add(stockLevel);
        storageUnitRepository.save(storageUnit);


    }

    public StockLevel findStockLevel(StorageUnit storageUnit, StockLevel stockLevel) {
        for (StockLevel stock : storageUnit.getStockLevels()) {
            if (stock.getThing().getId().equals(stockLevel.getThing().getId()))
                return stock;
        }
        return null;
    }


    public void removeFromStock(StorageUnit storageUnit, StockLevel stockLevel) {
        StockLevel found = findStockLevel(storageUnit, stockLevel);
        if (found == null || found.getQuantity() < stockLevel.getQuantity()) {
            return;
        }
        int newQuantity = found.getQuantity() - stockLevel.getQuantity();
        if (newQuantity == 0) {
            storageUnit.getStockLevels().remove(found);
            return;
        }

        found.setQuantity(newQuantity);

    }

    public void changeStockTo(StorageUnit storageUnit, StockLevel stockLevel) {
        StockLevel found = findStockLevel(storageUnit, stockLevel);
        if (found == null) {
            return;
        }

        int newQuantity = stockLevel.getQuantity();
        if (newQuantity < 0) {
            throw new ShopException("Quantity cannot be negative.");
        }

        found.setQuantity(newQuantity);
        if (newQuantity == 0) {
            storageUnit.getStockLevels().remove(found);
        }
    }

    public int getAvailableStock(StorageUnit storageUnit, Thing thing) {
        StockLevel stockLevel = new StockLevel(thing, 0);
        StockLevel found = findStockLevel(storageUnit, stockLevel);
        if (found == null) {
            return 0;
        }
        return found.getQuantity();
    }

    public int getDistanceToClient(StorageUnit storageUnit, ZipCode clientZipCode) {
        return storageUnit.getAddress().getZipCode().difference(clientZipCode);
    }

    public List<Thing> getAvailableItems(StorageUnit storageUnit, List<Thing> unfulfilledItems) {
        List<Thing> itemsFromStorageUnit = new ArrayList<>();
        for (StockLevel stockLevel : storageUnit.getStockLevels()) {
            if (unfulfilledItems.contains(stockLevel.getThing())) {
                itemsFromStorageUnit.add(stockLevel.getThing());
            }
        }
        return itemsFromStorageUnit;
    }


    public Integer getAvailableStockForItems(StorageUnit storageUnit, List<Thing> unfulfilledItems) {
        int totalAvailableStock = 0;
        for (StockLevel stockLevel : storageUnit.getStockLevels()) {
            if (unfulfilledItems.contains(stockLevel.getThing())) {
                totalAvailableStock += stockLevel.getQuantity();
            }
        }
        return totalAvailableStock;
    }


}
