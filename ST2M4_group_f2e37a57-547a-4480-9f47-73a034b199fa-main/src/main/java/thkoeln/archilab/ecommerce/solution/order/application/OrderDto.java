package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID orderId;
    private List<OrderPartDto> orderParts;
}
