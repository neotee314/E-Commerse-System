package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPartDto {
    private UUID thingId ;
    private int quantity = 0;

}
