package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShoppingBasketMapper {
    private final ShoppingBasketPartMapper mapper;

    public ShoppingBasket toShoppingBaskt(ShoppingBasketDto dto) {
        ShoppingBasket basket = new ShoppingBasket();
        basket.setId(dto.getId());
        List<ShoppingBasketPart> partList = new ArrayList<>();
        for (ShoppingBasketPartDto partDto : dto.getShoppingBasketParts()) {
            partList.add(mapper.toShoppingBasketPart(partDto));
        }
        basket.setParts(partList);
        return basket;

    }

    public ShoppingBasketDto toShoppingBasketDto(ShoppingBasket shoppingBasket) {

        ShoppingBasketDto dto = new ShoppingBasketDto();
        dto.setId(shoppingBasket.getId());
        List<ShoppingBasketPartDto> partDtoList = new ArrayList<>();
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            partDtoList.add(mapper.shoppingBasketPartDto(part));
        }
        dto.setShoppingBasketParts(partDtoList);
        return dto;
    }
}
