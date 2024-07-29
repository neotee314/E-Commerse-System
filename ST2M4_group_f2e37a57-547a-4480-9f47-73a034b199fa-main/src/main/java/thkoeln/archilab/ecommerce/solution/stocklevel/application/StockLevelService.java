package thkoeln.archilab.ecommerce.solution.stocklevel.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.solution.stocklevel.domain.StockLevel;
import thkoeln.archilab.ecommerce.solution.stocklevel.domain.StockLevelRepository;
import thkoeln.archilab.ecommerce.solution.thing.application.InventoryServiceInterface;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockLevelService implements InventoryServiceInterface {
    private final StockLevelRepository stockLevelRepository;

    public void save(StockLevel stockLevel) {
        stockLevelRepository.save(stockLevel);
    }

    public void removeFromStock(Thing thing, int removedQuantity) {
        List<StockLevel> stockLevelList = findAllContainingThing(thing);
        //wenn es mehrere stock gibt dann lösche die gebgebene Quantity von dem Stock der am meinsten quantity hat
        if (stockLevelList.size() >= 2) {
            int finalRemovedQuantity = removedQuantity;
            boolean hasMatchingStockLevel = stockLevelList.stream()
                    .anyMatch(stockLevel -> stockLevel.getQuantity() > finalRemovedQuantity + 1);

            if (hasMatchingStockLevel) {
                stockLevelList = stockLevelList.stream()
                        .filter(stockLevel -> stockLevel.getQuantity() > finalRemovedQuantity + 1)
                        .collect(Collectors.toList());
            }
        }
        Random random = new Random();

        while (removedQuantity > 0) {
            int i = random.nextInt(stockLevelList.size());
            int availableQuantity = stockLevelList.get(i).getQuantity();

            if (removedQuantity > availableQuantity && availableQuantity > 1) {
                removeFromQuantity(stockLevelList.get(i), 1);
                stockLevelRepository.save(stockLevelList.get(i));
                removedQuantity -= 1;
            } else if (availableQuantity >= removedQuantity) {
                removeFromQuantity(stockLevelList.get(i), removedQuantity);
                stockLevelRepository.save(stockLevelList.get(i));
                removedQuantity = 0;
            }
        }
    }

    public List<StockLevel> findAll() {
        List<StockLevel> stockLevels = new ArrayList<>();
        for (StockLevel stockLevel : stockLevelRepository.findAll()) {
            stockLevels.add(stockLevel);
        }
        return stockLevels;
    }

    public List<StockLevel> findAllContainingThing(Thing thing) {
        List<StockLevel> stockLevels = new ArrayList<>();
        for (StockLevel stockLevel : stockLevelRepository.findAll()) {
            if (contain(stockLevel, thing)) stockLevels.add(stockLevel);
        }
        return stockLevels;
    }

    public boolean isInInventory(Thing thing) {
        for (StockLevel stockLevel : stockLevelRepository.findAll()) {
            if (contain(stockLevel, thing)) return true;
        }
        return false;
    }

    public void removeFromQuantity(StockLevel stockLevel, int removedQuantity) {
        int current = stockLevel.getQuantity();
        stockLevel.setQuantity(current - removedQuantity);
        stockLevelRepository.save(stockLevel);
    }

    public void addToQuantity(StockLevel stockLevel, int addedQuantity) {
        int current = stockLevel.getQuantity();
        stockLevel.setQuantity(current + addedQuantity);
        stockLevelRepository.save(stockLevel);
    }


    public boolean contain(StockLevel stockLevel, Thing thing) {
        return stockLevel.getThing().getId().equals(thing.getId());
    }

    public void deleteAll() {
        stockLevelRepository.deleteAll();
    }
}
