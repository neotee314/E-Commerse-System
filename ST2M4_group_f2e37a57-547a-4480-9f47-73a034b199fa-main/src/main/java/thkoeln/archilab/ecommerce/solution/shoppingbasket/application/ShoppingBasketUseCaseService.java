package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.client.application.ClientService;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.order.application.OrderPartService;
import thkoeln.archilab.ecommerce.solution.order.application.OrderService;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketRepository;
import thkoeln.archilab.ecommerce.solution.stocklevel.application.StockLevelService;
import thkoeln.archilab.ecommerce.solution.storageunit.application.StorageUnitUseCaseService;
import thkoeln.archilab.ecommerce.solution.thing.application.ReservationServiceInterface;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.ShoppingBasketUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ShoppingBasketUseCaseService implements ShoppingBasketUseCases {

    private final ClientService clientService;
    private final ThingService thingService;
    private final ShoppingBasketManagementService shoppingBasketManagementService;
    private final BasketPartsManagementService partsManagementService;
    private final OrderService orderService;
    private final StorageUnitUseCaseService storageUnitUseCaseService;
    private final StockLevelService stockLevelService;
    private final OrderPartService orderPartService;
    private final ReservationServiceInterface reservationServiceInterface;
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final BasketPricingService basketPricingService;

    @Override
    public void addThingToShoppingBasket(EmailType clientEmail, UUID thingId, int quantity) {
        if (clientEmail == null) throw new ShopException("client email cannot be null");
        Client client = clientService.findByEmail(clientEmail);
        if (client == null) throw new ShopException("Client with does not exist");
        if (quantity < 0) throw new ShopException("quantity cannot be null");
        if (thingId == null) throw new ShopException("Thing id cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        int currentInventory = storageUnitUseCaseService.getAvailableStock(thing.getId());
        if (currentInventory < quantity)
            throw new ShopException("Thing is not available in the requested quantity");
        stockLevelService.removeFromStock(thing, quantity);
        ShoppingBasketPart part = new ShoppingBasketPart(thing, quantity);
        partsManagementService.addPart(client, part);

    }

    @Override
    public void removeThingFromShoppingBasket(EmailType clientEmail, UUID thingId, int quantity) {
        if (clientEmail == null) throw new ShopException("client email cannot be null");
        Client client = clientService.findByEmail(clientEmail);
        if (client == null) throw new ShopException("Client  does not exist");
        if (thingId == null) throw new ShopException("Thing id can not be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        if (quantity < 0) throw new ShopException("quantity can not be negative");

        ShoppingBasket basket = shoppingBasketRepository.findByClient(client);
        if (basket == null) throw new ShopException("Basket doese not exist");
        if (!shoppingBasketManagementService.contains(basket, thing))
            throw new ShopException("Thing is not in the cart of client");
        int currentlyReservedGood = reservationServiceInterface.getReservedQuantity(basket.getId(), thing);
        if (quantity > currentlyReservedGood)
            throw new ShopException("Thing is not in the cart in the requested quantity");
        partsManagementService.removeThingFromBasket(basket, thing, quantity);
    }

    @Override
    public Map<UUID, Integer> getShoppingBasketAsMap(EmailType clientEmail) {
        if (clientEmail == null) throw new ShopException("email cannot be null");
        Client client = clientService.findByEmail(clientEmail);
        if (client == null) throw new ShopException("client does not exist");
        ShoppingBasket basket = shoppingBasketRepository.findByClient(client);

        return shoppingBasketManagementService.getAsMap(basket);
    }

    @Override
    public MoneyType getShoppingBasketAsMoneyValue(EmailType clientEmail) {
        if (clientEmail == null) throw new ShopException("email cannot be null");
        Client client = clientService.findByEmail(clientEmail);
        if (client == null) throw new ShopException("client does not exist");
        ShoppingBasket basket = shoppingBasketRepository.findByClient(client);
        return basketPricingService.getCartValue(basket);
    }

    @Override
    public int getReservedStockInShoppingBaskets(UUID thingId) {
        if (thingId == null) throw new ShopException("Thing id cannot be null");
        Thing thing = thingService.findById(thingId);
        if (thing == null) throw new ShopException("Thing does not exist");
        List<ShoppingBasket> shoppingBasketList = shoppingBasketManagementService.findAll();
        return shoppingBasketList.stream().mapToInt(basket ->
                reservationServiceInterface.getReservedQuantity(basket.getId(), thing)).sum();
    }


    @Override
    public boolean isEmpty(EmailType clientEmail) {
        if (clientEmail == null) throw new ShopException("email cannot be null");
        Client client = clientService.findByEmail(clientEmail);
        if (client == null) throw new ShopException("client does not exist");
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClient(client);
        if (shoppingBasket == null) return true;
        return shoppingBasketManagementService.isEmpty(shoppingBasket);
    }


    @Override
    public UUID checkout(EmailType clientEmail) {
        if (clientEmail == null) throw new ShopException("client email cannot be null");
        Client client = clientService.findByEmail(clientEmail);
        if (client == null) throw new ShopException("Client does not exist");
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClient(client);
        if (shoppingBasket == null || shoppingBasketManagementService.isEmpty(shoppingBasket))
            throw new ShopException("Shopping basket is empty");
        List<ShoppingBasketPart> parts = shoppingBasket.getParts();
        List<OrderPart> orderParts = new ArrayList<>();
        parts.forEach(part -> orderParts.add(orderPartService.createOrderPart(part.getThing(), part.getQuantity())));
        Order order = orderService.create(orderParts);
        clientService.addToOrderHistory(client, order);
        shoppingBasketManagementService.emptyBasket(shoppingBasket);
        return order.getOrderId();
    }

    @Override
    public void emptyAllShoppingBaskets() {
        shoppingBasketManagementService.emptyAllBaskets();
    }

}
