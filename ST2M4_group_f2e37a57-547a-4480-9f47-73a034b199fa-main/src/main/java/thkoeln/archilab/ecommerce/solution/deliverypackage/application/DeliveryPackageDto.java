package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Getter
public class DeliveryPackageDto {
    private UUID id;
    private UUID storageUnitId;
    private UUID orderId;
    private List<DeliveryPackagePartDto> deliveryPackageParts;

    public DeliveryPackageDto(UUID id, UUID storageUnitId, UUID orderId, List<DeliveryPackagePartDto> deliveryPackageParts) {
        this.id = id;
        this.storageUnitId = storageUnitId;
        this.orderId = orderId;
        this.deliveryPackageParts = deliveryPackageParts;
    }
}
