package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;

@Service
@RequiredArgsConstructor
public class BasketPricingService {
    private final BasketPartsManagementService basketPartsManagementService;

    public MoneyType getCartValue(ShoppingBasket shoppingBasket) {
        if (shoppingBasket == null) throw new ShopException("invalid data");
        float totalValue = 0;
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            totalValue += basketPartsManagementService.getSellingPrice(part).multiplyBy(part.getQuantity()).getAmount();
        }
        return Money.of(totalValue, "EUR");
    }

    public String getTotalSalesPriceString(ShoppingBasket shoppingBasket) {
        MoneyType totalValue = getCartValue(shoppingBasket);
        return String.format("%.2f €", totalValue.getAmount());
    }
}
