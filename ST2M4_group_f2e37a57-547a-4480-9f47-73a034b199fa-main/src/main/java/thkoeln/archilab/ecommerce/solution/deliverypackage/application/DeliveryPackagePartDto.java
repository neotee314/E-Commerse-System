package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Getter
public class DeliveryPackagePartDto {
    private UUID thingId;
    private int quantity;

    public DeliveryPackagePartDto(UUID thingId, int quantity) {
        this.thingId = thingId;
        this.quantity = quantity;
    }
}
