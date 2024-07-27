package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePart;
import thkoeln.archilab.ecommerce.solution.order.application.OrderService;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.application.ShoppingBasketDTO;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DeliveryPackageController {

    private final DeliveryUseCaseService deliveryUseCaseService;
    private final OrderService orderService;

    @GetMapping("/deliveryPackages")
    public ResponseEntity<List<DeliveryPackageDto>> getAllDeliveryPackage(
            @RequestParam(value = "orderId", required = false) UUID orderId) {
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        Order order = orderService.findById(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            List<UUID> storageUUIDs = deliveryUseCaseService.getContributingStorageUnitsForOrder(orderId);

            List<DeliveryPackageDto> response = storageUUIDs.stream().map(storageId -> {
                Map<UUID, Integer> content = deliveryUseCaseService.getDeliveryPackageForOrderAndStorageUnit(orderId, storageId);

                List<DeliveryPackagePartDto> parts = content.entrySet().stream().map(entry ->
                        new DeliveryPackagePartDto(entry.getKey(), entry.getValue())
                ).collect(Collectors.toList());

                return new DeliveryPackageDto(UUID.randomUUID(), storageId, orderId, parts);
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
