package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPartRepository;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPartRepository;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketRepository;
import thkoeln.archilab.ecommerce.solution.thing.application.ReservationServiceInterface;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationServiceInterface {
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ShoppingBasketService shoppingBasketService;
    private final OrderPartRepository orderPartRepository;
    private final ShoppingBasketPartRepository shoppingBasketPartRepository;


    @Override
    public int getTotalReservedInAllBaskets(Thing thing) {
        List<ShoppingBasketPart> orderPartContainGoodList = shoppingBasketService.findAllPartsContaining(thing);
        int totalReserved = 0;
        for (ShoppingBasketPart orderPart : orderPartContainGoodList) {
            totalReserved += orderPart.getQuantity();
        }
        return totalReserved;
    }

    @Override
    public void deleteOrderParts() {
        orderPartRepository.deleteAll();
    }

    @Override
    public void deleteAllShoppingBasketParts() {
        shoppingBasketPartRepository.deleteAll();
    }


    @Override
    public void removeFromReservedQuantity(Thing thing, int removedQuantity) {
        if (thing == null || removedQuantity < 0) {
            throw new ShopException("Thing does not exist or Quantity is negative");
        }

        List<ShoppingBasket> basketsContainingThing = shoppingBasketService.getAllBasketContaining(thing);
        if (basketsContainingThing.isEmpty()) return;
        Random random = new Random();

        int totalReserved = 0;

        for (ShoppingBasket basket : basketsContainingThing) {
            totalReserved += basket.getReservedQuantity(thing);
        }
        if (totalReserved < removedQuantity)
            throw new ShopException("cannot remvoe more than reserved thing in basket");
        while (removedQuantity > 0) {
            int randomIndex = random.nextInt(basketsContainingThing.size());
            ShoppingBasket basket = basketsContainingThing.get(randomIndex);
            boolean isRemoved = basket.removeThingFromBasket(thing, 1);
            shoppingBasketRepository.save(basket);
            if (isRemoved) removedQuantity -= 1;
        }
    }


    @Override
    public boolean isReserved(Thing thing) {
        return shoppingBasketService.contains(thing);
    }

}
