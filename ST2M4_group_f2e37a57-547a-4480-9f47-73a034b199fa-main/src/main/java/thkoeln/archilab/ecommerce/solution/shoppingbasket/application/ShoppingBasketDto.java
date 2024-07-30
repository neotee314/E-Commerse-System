package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShoppingBasketDto {

    private UUID id ;
    private String totalSalesPrice ;
    private List<ShoppingBasketPartDto> shoppingBasketParts ;

}
