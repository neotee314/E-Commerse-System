package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.domainprimitives.ZipCode;
import thkoeln.archilab.ecommerce.solution.client.application.ClientService;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePart;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePartRepository;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageRepository;
import thkoeln.archilab.ecommerce.solution.order.application.OrderService;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.storageunit.application.StorageUnitService;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.*;
import java.util.stream.Collectors;

import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.STORAGE_UNIT_NUMOF;

@Service
@RequiredArgsConstructor
public class DeliveryPackageService {

    private final ClientService clientService;
    private final StorageUnitService storageUnitService;
    private final DeliveryPackageRepository deliveryPackageRepository;
    private final OrderService orderService;


    public List<UUID> getContributingStorageUnits(List<Thing> unfulfilledItems, Order order) {
        List<UUID> contributingStorageUnits = new ArrayList<>();
        ZipCode clientZipCode = (ZipCode) clientService.findClientByOrder(order).getHomeAddress().getZipCode();


        List<StorageUnit> allStorageUnits = storageUnitService.findAll();


        while (!unfulfilledItems.isEmpty()) {

            List<StorageUnit> sortedStorageUnits = sortStorageUnits(allStorageUnits, unfulfilledItems, clientZipCode);
            StorageUnit selectedStorageUnit = sortedStorageUnits.get(0);


            List<Thing> itemsFromStorageUnit = storageUnitService.getAvailableItems(selectedStorageUnit, unfulfilledItems);

            boolean selected = true;
            for (Thing thing : itemsFromStorageUnit) {
                int availableQuantity = storageUnitService.getAvailableStock(selectedStorageUnit, thing);
                int orderQuantityOfThing = orderService.getOrderPartContain(order, thing).getOrderQuantity();
                if (availableQuantity < orderQuantityOfThing) {
                    selected = false;
                    break;
                }
            }
            if (selected) {
                for (Thing thing : itemsFromStorageUnit) {
                    unfulfilledItems.remove(thing);
                }
                contributingStorageUnits.add(selectedStorageUnit.getStorageId());
            }
            if (!selected) allStorageUnits.remove(selectedStorageUnit);

        }
        return contributingStorageUnits;
    }


    private List<StorageUnit> sortStorageUnits(List<StorageUnit> allStorageUnits, List<Thing> unfulfilledItems, ZipCode clientZipCode) {

        allStorageUnits.sort((su1, su2) -> {
            int compareAvailableItems = Integer.compare(
                    storageUnitService.getAvailableItems(su2, unfulfilledItems).size(),
                    storageUnitService.getAvailableItems(su1, unfulfilledItems).size()
            );

            if (compareAvailableItems != 0) {
                return compareAvailableItems;
            }

            return Integer.compare(
                    storageUnitService.getDistanceToClient(su1, clientZipCode),
                    storageUnitService.getDistanceToClient(su2, clientZipCode)
            );
        });

        return allStorageUnits;
    }

    public List<Thing> getUnfulfilledItems(Order order) {
        List<Thing> unfulfilledItems = new ArrayList<>();
        order.getOrderParts().forEach(orderPart -> unfulfilledItems.add(orderPart.getThing()));
        return unfulfilledItems;
    }

    public List<Thing> getItemsFromStorageUnit(StorageUnit storageUnit, List<Thing> unfulfilledItems) {
        return storageUnitService.getAvailableItems(storageUnit, unfulfilledItems);
    }

    public DeliveryPackage createDeliveryPackage(Order order, List<Thing> itemsFromStorageUnit) {
        DeliveryPackage deliveryPackage = new DeliveryPackage();
        for (Thing thing : itemsFromStorageUnit) {
            OrderPart orderPart = orderService.getOrderPartContain(order, thing);
            DeliveryPackagePart part = new DeliveryPackagePart(thing, orderPart.getOrderQuantity());
            deliveryPackage.getParts().add(part);
            deliveryPackageRepository.save(deliveryPackage);
        }
        return deliveryPackage;
    }

    public DeliveryPackage optimizeDeliveryPackage(DeliveryPackage deliveryPackage) {
        DeliveryPackagePart foundBigPart = findBigPart(deliveryPackage);
        if (foundBigPart != null) {
            DeliveryPackage bigDeliveryPackage = new DeliveryPackage();
            bigDeliveryPackage.getParts().add(foundBigPart);
            return bigDeliveryPackage;
        }
        return deliveryPackage;
    }

    public DeliveryPackagePart findBigPart(DeliveryPackage deliveryPackage) {
        for (DeliveryPackagePart part : deliveryPackage.getParts()) {
            if (part.getQuantity() > 5) {
                return part;
            }
        }
        return null;
    }

    public Map<UUID, Integer> createDeliveryPackageMap(DeliveryPackage deliveryPackage) {
        Map<UUID, Integer> deliveryPackageMap = new HashMap<>();
        for (DeliveryPackagePart part : deliveryPackage.getParts()) {
            deliveryPackageMap.put(part.getThing().getId(), part.getQuantity());
        }
        return deliveryPackageMap;
    }


}
