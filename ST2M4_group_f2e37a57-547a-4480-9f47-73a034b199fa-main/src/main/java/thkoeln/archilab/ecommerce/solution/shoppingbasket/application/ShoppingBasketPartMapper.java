package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import org.springframework.stereotype.Component;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;

@Component
public class ShoppingBasketPartMapper {

    public ShoppingBasketPart toShoppingBasketPart(ShoppingBasketPartDto dto) {
        ShoppingBasketPart part = new ShoppingBasketPart();
        part.setQuantity(dto.getQuantity());
        part.setId(dto.getThingId());
        return part;
    }

    public ShoppingBasketPartDto shoppingBasketPartDto(ShoppingBasketPart part) {
        ShoppingBasketPartDto dto = new ShoppingBasketPartDto();
        dto.setQuantity(part.getQuantity());
        dto.setThingId(part.getThing().getId());
        return dto;

    }
}
