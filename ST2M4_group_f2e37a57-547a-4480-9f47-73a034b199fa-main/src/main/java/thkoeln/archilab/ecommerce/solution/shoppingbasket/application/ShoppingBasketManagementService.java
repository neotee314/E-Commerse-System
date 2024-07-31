package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.client.application.ClientService;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketRepository;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoppingBasketManagementService {
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final BasketPricingService basketPricingService;
    private final ShoppingBasketMapper mapper;
    private final ClientService clientService;

    public ShoppingBasket create(Client client) {
        if (client == null) throw new ShopException("client does not exist");
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setClient(client);
        shoppingBasketRepository.save(shoppingBasket);
        return shoppingBasket;
    }


    public ShoppingBasket findById(UUID shoppingBasketId) {
        return shoppingBasketRepository.findById(shoppingBasketId).orElse(null);
    }

    public List<ShoppingBasket> findAll() {
        return shoppingBasketRepository.findAll();
    }

    public void emptyBasket(ShoppingBasket shoppingBasket) {
        if (shoppingBasket == null) throw new ShopException("invalid data");
        shoppingBasket.getParts().clear();
        shoppingBasketRepository.save(shoppingBasket);
    }

    public void emptyAllBaskets() {
        shoppingBasketRepository.deleteAll();
    }

    public boolean isEmpty(ShoppingBasket shoppingBasket) {
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            if (part.getQuantity() != 0) {
                return false;
            }
        }
        return true;
    }

    public Map<UUID, Integer> getAsMap(ShoppingBasket shoppingBasket) {
        Map<UUID, Integer> basketAsMap = new HashMap<>();
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            basketAsMap.put(part.getThing().getId(), part.getQuantity());
        }
        return basketAsMap;
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

    public List<ShoppingBasket> getAllBasketContaining(Thing thing) {
        List<ShoppingBasket> shoppingBaskets = findAll();
        List<ShoppingBasket> result = new ArrayList<>();
        for (ShoppingBasket basket : shoppingBaskets) {
            if (contains(basket, thing)) {
                result.add(basket);
            }
        }
        return result;
    }

    public boolean contains(ShoppingBasket shoppingBasket, Thing thing) {
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            if (part.getThing().getId().equals(thing.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Thing thing) {
        if (thing == null) throw new ShopException("thing cannot be null");
        List<ShoppingBasket> shoppingBaskets = shoppingBasketRepository.findAll();
        return shoppingBaskets.stream().anyMatch(basket -> contains(basket, thing));
    }

    public List<ShoppingBasketPart> findAllPartsContaining(Thing thing) {
        List<ShoppingBasket> shoppingBaskets = getAllBasketContaining(thing);
        List<ShoppingBasketPart> result = new ArrayList<>();
        for (ShoppingBasket basket : shoppingBaskets) {
            result.addAll(getPartsContainingThing(basket, thing));
        }
        return result;
    }

    public List<ShoppingBasketPart> getPartsContainingThing(ShoppingBasket shoppingBasket, Thing thing) {
        List<ShoppingBasketPart> result = new ArrayList<>();
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            if (part.getThing().getId().equals(thing.getId())) {
                result.add(part);
            }
        }
        return result;
    }


    public ShoppingBasketDto findBasketByClientId(UUID clientId) {
        Client client = clientService.findClientById(clientId);
        if (client == null) return null;

        ShoppingBasket shoppingBasket = findOrCreateBasketByClient(client);

        ShoppingBasketDto shoppingBasketDTO = mapper.toShoppingBasketDto(shoppingBasket);
        String totalSalesPriceString = basketPricingService.getTotalSalesPriceString(shoppingBasket);
        shoppingBasketDTO.setTotalSalesPrice(totalSalesPriceString);

        return shoppingBasketDTO;
    }
}

