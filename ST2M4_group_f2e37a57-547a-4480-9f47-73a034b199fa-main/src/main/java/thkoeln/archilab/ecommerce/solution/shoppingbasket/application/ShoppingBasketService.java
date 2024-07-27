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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ShoppingBasketService {

    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ClientService clientService;

    public ShoppingBasket create(Client client) {
        if (client == null) throw new ShopException("client does not exist");
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setClient(client);
        shoppingBasketRepository.save(shoppingBasket);
        return shoppingBasket;
    }

    public void addPart(Client client, ShoppingBasketPart part) {
        if (client == null || part == null) throw new ShopException("invalid data");
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClient(client);
        if (shoppingBasket == null) shoppingBasket = create(client);
        shoppingBasket.addPart(part);
        shoppingBasketRepository.save(shoppingBasket);
    }

    public void removeThingFromBasket(ShoppingBasket basket, Thing thing, int quantity) {
        if (basket == null || thing == null || quantity < 0)
            throw new ShopException("invalid data");
        basket.removeThingFromBasket(thing, quantity);
        shoppingBasketRepository.save(basket);
    }

    public ShoppingBasket findBasketByClient(Client client) {
        if (client == null) throw new ShopException("invalid data");
        return shoppingBasketRepository.findByClient(client);
    }


    public boolean contains(Thing thing) {
        if (thing == null) throw new ShopException("thing cannot be null");
        List<ShoppingBasket> shoppingBaskets = shoppingBasketRepository.findAll();
        return shoppingBaskets.stream().anyMatch(basket -> basket.contains(thing));
    }

    public void emptyBasket(ShoppingBasket shoppingBasket) {
        if (shoppingBasket == null) throw new ShopException("invalid data");
        shoppingBasket.emptyBasket();
        shoppingBasketRepository.save(shoppingBasket);
    }

    public void emptyAllBaskets() {
        shoppingBasketRepository.deleteAll();

    }

    public List<ShoppingBasket> findAll() {
        return shoppingBasketRepository.findAll();
    }

    public List<ShoppingBasketPart> findAllPartsContaining(Thing thing) {
        List<ShoppingBasket> shoppingBaskets = getAllBasketContaining(thing);
        List<ShoppingBasketPart> result = new ArrayList<>();
        for (ShoppingBasket basket : shoppingBaskets) {
            result.addAll(basket.getOrderPartsContaining(thing));
        }
        return result;
    }

    public List<ShoppingBasket> getAllBasketContaining(Thing thing) {
        List<ShoppingBasket> shoppingBaskets = findAll();
        List<ShoppingBasket> result = new ArrayList<>();
        for (ShoppingBasket basket : shoppingBaskets) {
            if (basket.contains(thing)) {
                result.add(basket);
            }
        }
        return result;
    }
    public ShoppingBasket findOrCreateBasketByClient(Client client) {
        ShoppingBasket shoppingBasket = findBasketByClient(client);
        if (shoppingBasket == null) {
            ShoppingBasket basket = new ShoppingBasket();
            basket.setClient(client);
            shoppingBasketRepository.save(basket);
            return basket;
        }
        return shoppingBasket;
    }


    public ShoppingBasketDTO findBasketByClientId(UUID clientId) {
        Client client = clientService.findClientById(clientId);
        if (client == null) return null;

        ShoppingBasket shoppingBasket = findOrCreateBasketByClient(client);

        ShoppingBasketDTO shoppingBasketDTO = new ShoppingBasketDTO();
        shoppingBasketDTO.setId(shoppingBasket.getId());

        List<ShoppingBasketPartDTO> partDTOS = new ArrayList<>();
        for (ShoppingBasketPart part : shoppingBasket.getParts()) {
            ShoppingBasketPartDTO partDto = new ShoppingBasketPartDTO();
            partDto.setQuantity(part.getQuantity());
            partDto.setThingId(part.getThing().getId());
            partDTOS.add(partDto);
        }

        shoppingBasketDTO.setShoppingBasketParts(partDTOS);
        String totalSalesPriceString = String.format("%.2f €", shoppingBasket.getCartValue().getAmount());
        shoppingBasketDTO.setTotalSalesPrice(totalSalesPriceString);

        return shoppingBasketDTO;
    }

    public ShoppingBasket findById(UUID shoppingBasketId) {
        Optional<ShoppingBasket> shoppingBasketOptional = shoppingBasketRepository.findById(shoppingBasketId);
        if (shoppingBasketOptional.isEmpty()) return null;
        return shoppingBasketOptional.get();
    }




}
