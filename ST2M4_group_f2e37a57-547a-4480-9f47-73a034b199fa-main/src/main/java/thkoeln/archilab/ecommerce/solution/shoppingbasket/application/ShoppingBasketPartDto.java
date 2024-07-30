package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingBasketPartDto {
    private UUID thingId ;
    private int quantity = 0;
}
