package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPartDTO {
    private UUID thingId ;
    private int quantity = 0;

}
