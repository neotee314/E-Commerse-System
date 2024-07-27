package thkoeln.archilab.ecommerce.solution.storageunit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.domainprimitives.ZipCode;
import thkoeln.archilab.ecommerce.solution.stocklevel.domain.StockLevel;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class StorageUnit {
    @Id
    private UUID storageId = UUID.randomUUID();

    private String name;
    @Embedded
    private HomeAddress address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<StockLevel> stockLevels = new ArrayList<>();

    public StorageUnit(HomeAddressType address, String name) {
        this.address = (HomeAddress) address;
        this.name = name;
    }

    public void addToStock(StockLevel stockLevel) {

        StockLevel found = findStockLevel(stockLevel);
        if (found != null) {
            found.addToQuantity(stockLevel.getQuantity());
            return;
        }

        stockLevels.add(stockLevel);

    }

    public StockLevel findStockLevel(StockLevel stockLevel) {
        for (StockLevel stock : stockLevels) {
            if (stock.getThing().getId().equals(stockLevel.getThing().getId()))
                return stock;
        }
        return null;
    }


    public void removeFromStock(StockLevel stockLevel) {
        StockLevel found = findStockLevel(stockLevel);
        if (found == null || found.getQuantity() < stockLevel.getQuantity()) {
            return;
        }
        int newQuantity = found.getQuantity() - stockLevel.getQuantity();
        if (newQuantity == 0) {
            stockLevels.remove(found);
            return;
        }

        found.setQuantity(newQuantity);

    }

    public void changeStockTo(StockLevel stockLevel) {
        StockLevel found = findStockLevel(stockLevel);
        if (found == null) {
            return;
        }

        int newQuantity = stockLevel.getQuantity();
        if (newQuantity < 0) {
            throw new ShopException("Quantity cannot be negative.");
        }

        found.setQuantity(newQuantity);
        if (newQuantity == 0) {
            stockLevels.remove(found);
        }
    }

    public int getAvailableStock(Thing thing) {
        StockLevel stockLevel = new StockLevel(thing, 0);
        StockLevel found = findStockLevel(stockLevel);
        if (found == null) {
            return 0;
        }
        return found.getQuantity();
    }

    public int getDistanceToClient(ZipCode clientZipCode) {
        return this.address.getZipCode().difference(clientZipCode);
    }

    public List<Thing> getAvailableItems(List<Thing> unfulfilledItems) {
        List<Thing> itemsFromStorageUnit = new ArrayList<>();
        for (StockLevel stockLevel : stockLevels) {
            if (unfulfilledItems.contains(stockLevel.getThing())) {
                itemsFromStorageUnit.add(stockLevel.getThing());
            }
        }
        return itemsFromStorageUnit;
    }


    public Integer getAvailableStockForItems(List<Thing> unfulfilledItems) {
        int totalAvailableStock = 0;
        for (StockLevel stockLevel : stockLevels) {
            if (unfulfilledItems.contains(stockLevel.getThing())) {
                totalAvailableStock += stockLevel.getQuantity();

            }
        }
        return totalAvailableStock;
    }


    public List<Thing> getAllThing() {
        List<Thing> things = new ArrayList<>();
        for (StockLevel stockLevel : stockLevels) {
            things.add(stockLevel.getThing());
        }
        return things;
    }
}
