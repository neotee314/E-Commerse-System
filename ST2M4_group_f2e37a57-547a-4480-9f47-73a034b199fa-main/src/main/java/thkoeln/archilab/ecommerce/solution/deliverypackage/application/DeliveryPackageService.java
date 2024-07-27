package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.domainprimitives.ZipCode;
import thkoeln.archilab.ecommerce.solution.client.application.ClientService;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePart;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePartRepository;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageRepository;
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


    public List<UUID> getContributingStorageUnits(List<Thing> unfulfilledItems, Order order) {
        List<UUID> contributingStorageUnits = new ArrayList<>();
        ZipCode clientZipCode = (ZipCode) clientService.findClientByOrder(order).getHomeAddress().getZipCode();


        List<StorageUnit> allStorageUnits = storageUnitService.findAll();


        while (!unfulfilledItems.isEmpty()) {

            List<StorageUnit> sortedStorageUnits = sortStorageUnits(allStorageUnits, unfulfilledItems, clientZipCode);
            StorageUnit selectedStorageUnit = sortedStorageUnits.get(0);


            List<Thing> itemsFromStorageUnit = selectedStorageUnit.getAvailableItems(unfulfilledItems);

            boolean selected = true;
            for (Thing thing : itemsFromStorageUnit) {
                int availableQuantity = selectedStorageUnit.getAvailableStock(thing);
                int orderQuantityOfThing = order.getOrderPartContain(thing).getOrderQuantity();
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
                    su2.getAvailableItems(unfulfilledItems).size(),
                    su1.getAvailableItems(unfulfilledItems).size()
            );

            if (compareAvailableItems != 0) {
                return compareAvailableItems;
            }

            return Integer.compare(
                    su1.getDistanceToClient(clientZipCode),
                    su2.getDistanceToClient(clientZipCode)
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
        return storageUnit.getAvailableItems(unfulfilledItems);
    }

    public DeliveryPackage createDeliveryPackage(Order order, List<Thing> itemsFromStorageUnit) {
        DeliveryPackage deliveryPackage = new DeliveryPackage();
        for (Thing thing : itemsFromStorageUnit) {
            OrderPart orderPart = order.getOrderPartContain(thing);
            DeliveryPackagePart part = new DeliveryPackagePart(thing, orderPart.getOrderQuantity());
            deliveryPackage.addPart(part);
            deliveryPackageRepository.save(deliveryPackage);
        }
        return deliveryPackage;
    }

    public DeliveryPackage optimizeDeliveryPackage(DeliveryPackage deliveryPackage) {
        DeliveryPackagePart foundBigPart = findBigPart(deliveryPackage);
        if (foundBigPart != null) {
            DeliveryPackage bigDeliveryPackage = new DeliveryPackage();
            bigDeliveryPackage.addPart(foundBigPart);
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
