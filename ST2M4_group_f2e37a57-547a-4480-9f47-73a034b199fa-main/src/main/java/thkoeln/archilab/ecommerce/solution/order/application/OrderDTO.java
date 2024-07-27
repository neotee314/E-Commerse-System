package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID orderId;
    private List<OrderPartDTO> orderParts;
}
