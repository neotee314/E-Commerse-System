package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.client.application.ClientService;
import thkoeln.archilab.ecommerce.solution.client.application.OrderHistoryServiceInterface;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.client.domain.Orderable;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderRepository;
import thkoeln.archilab.ecommerce.solution.thing.application.OrderedThingServiceInterface;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import java.util.*;


@Service
@RequiredArgsConstructor
public class OrderService implements OrderHistoryServiceInterface, OrderedThingServiceInterface {

    private final OrderRepository orderRepository;
    private final ClientService clientService;


    public Order create(List<OrderPart> orderParts) {
        Order order = new Order();
        for (OrderPart part : orderParts) {
            order.addOrderPart(part);
        }
        orderRepository.save(order);
        return order;
    }


    @Override
    public Map<UUID, Integer> getOrderHistory(EmailType clientEmail) {
        if (clientEmail == null) {
            throw new ShopException("email is null");
        }

        Client client = clientService.findByEmail(clientEmail);
        if (client == null) {
            throw new ShopException("client does not exist");
        }

        Map<UUID, Integer> orderHistoryMap = new HashMap<>();
        List<Order> orders = mapToOrder(clientService.getOrderHistory(client));

        for (Order order : orders) {
            List<OrderPart> orderParts = order.getOrderParts();

            for (OrderPart orderPart : orderParts) {
                UUID thingId = orderPart.getThingId();
                int quantity = orderPart.getOrderQuantity();
                if (orderHistoryMap.containsKey(thingId)) {
                    orderHistoryMap.put(thingId, orderHistoryMap.get(thingId) + quantity);
                } else {
                    orderHistoryMap.put(thingId, quantity);
                }
            }
        }

        return orderHistoryMap;
    }

    private List<Order> mapToOrder(List<Orderable> orderableList) {
        List<Order> orders = new ArrayList<>();
        for (Orderable clientOrder : orderableList) {
            orders.add((Order) clientOrder);
        }
        return orders;
    }

    public boolean existsById(UUID orderId) {
        return !orderRepository.existsById(orderId);
    }

    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public boolean isPartOfCompletedOrder(Thing thing) {
        if (thing == null) throw new ShopException("thing cannot be null");
        List<Order> orders = orderRepository.findAll();
        return orders.stream().anyMatch(order -> order.contains(thing));
    }
}
