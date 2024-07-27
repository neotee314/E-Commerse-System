package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePart;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePartRepository;
import thkoeln.archilab.ecommerce.solution.order.application.OrderService;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageRepository;
import thkoeln.archilab.ecommerce.solution.storageunit.application.StorageUnitService;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.DeliveryPackageUseCases;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryUseCaseService implements DeliveryPackageUseCases {

    private final OrderService orderService;
    private final StorageUnitService storageUnitService;
    private final DeliveryPackageRepository deliveryPackageRepository;
    private final DeliveryPackageService deliveryPackageService;

    @Override
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        if (orderId == null) {
            throw new ShopException("OrderId cannot be null.");
        }
        if (orderService.existsById(orderId)) {
            throw new ShopException("Order with Id " + orderId + " does not exist.");
        }
        Order order = orderService.findById(orderId);

        List<Thing> unfulfilledItems = deliveryPackageService.getUnfulfilledItems(order);
        return deliveryPackageService.getContributingStorageUnits(unfulfilledItems, order);
    }


    @Override
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {

        if (orderId == null || storageUnitId == null || orderService.existsById(orderId))
            throw new ShopException("invalid data");
        Order order = orderService.findById(orderId);
        StorageUnit storageUnit = storageUnitService.findById(storageUnitId);
        List<Thing> unfulfilledItems = deliveryPackageService.getUnfulfilledItems(order);
        List<Thing> itemsFromStorageUnit = deliveryPackageService.getItemsFromStorageUnit(storageUnit, unfulfilledItems);

        DeliveryPackage deliveryPackage = deliveryPackageService.createDeliveryPackage(order, itemsFromStorageUnit);
        DeliveryPackage optimizedDeliveryPackage = deliveryPackageService.optimizeDeliveryPackage(deliveryPackage);
        return deliveryPackageService.createDeliveryPackageMap(optimizedDeliveryPackage);

    }

    @Override
    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}
