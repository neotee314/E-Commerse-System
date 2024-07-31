package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPartRepository;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketRepository;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasketPartsManagementService {

    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ShoppingBasketPartRepository shoppingBasketPartRepository;

    public void addPart(ShoppingBasket shoppingBasket, ShoppingBasketPart newPart) {
        if (shoppingBasket == null || newPart == null) throw new ShopException("invalid data");
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            if (part.getThing().getId().equals(newPart.getThing().getId())) {
                addQuantity(part, newPart.getQuantity());
                shoppingBasketRepository.save(shoppingBasket);
                return;
            }
        }
        shoppingBasket.getParts().add(new ShoppingBasketPart(newPart.getThing(), newPart.getQuantity()));
        shoppingBasketRepository.save(shoppingBasket);
    }
    public void addPart(Client client, ShoppingBasketPart part) {
        if (client == null || part == null) throw new ShopException("invalid data");
        ShoppingBasket shoppingBasket = findOrCreateBasketByClient(client);
        addPart(shoppingBasket, part);
   }
    public ShoppingBasket findOrCreateBasketByClient(Client client) {
        if (client == null) throw new ShopException("invalid data");
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClient(client);
        if (shoppingBasket == null) {
            ShoppingBasket basket = new ShoppingBasket();
            basket.setClient(client);
            shoppingBasketRepository.save(basket);
            return basket;
        }
        return shoppingBasket;
    }

    public boolean removeThingFromBasket(ShoppingBasket shoppingBasket, Thing thing, int quantity) {
        if (shoppingBasket == null || thing == null) throw new ShopException("invalid data");
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            if (contains(part, thing) && part.getQuantity() >= quantity) {
                removeQuantity(part, quantity);
                if (part.getQuantity() == 0) {
                    shoppingBasket.getParts().remove(part);
                }
                shoppingBasketRepository.save(shoppingBasket);
                return true;
            }
        }
        shoppingBasketRepository.save(shoppingBasket);
        return false;
    }


    public boolean contains(ShoppingBasketPart part, Thing thing) {
        return part.getThing().getId().equals(thing.getId());
    }


    public void addQuantity(ShoppingBasketPart part, int quantity) {
        int current = part.getQuantity();
        part.setQuantity(current + quantity);
        shoppingBasketPartRepository.save(part);

    }

    public void removeQuantity(ShoppingBasketPart part, int quantity) {
        int current = part.getQuantity();
        part.setQuantity(current - quantity);
        shoppingBasketPartRepository.save(part);
    }

    public Money getSellingPrice(ShoppingBasketPart part) {
        return part.getThing().getSellingPrice();
    }
}
