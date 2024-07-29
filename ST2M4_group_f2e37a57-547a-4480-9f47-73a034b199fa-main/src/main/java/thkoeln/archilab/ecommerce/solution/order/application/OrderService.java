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
    private final OrderPartService orderPartService;


    public Order create(List<OrderPart> orderParts) {
        Order order = new Order();
        for (OrderPart part : orderParts) {
            addOrderPart(order, part);
        }
        orderRepository.save(order);
        return order;
    }

    public void addOrderPart(Order order, OrderPart orderPart) {
        for (OrderPart o : order.getOrderParts()) {
            if (o == orderPart) {
                orderPartService.addQuantity(orderPart,orderPart.getOrderQuantity());
                return;
            }
        }
        order.getOrderParts().add(orderPart);
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
                UUID thingId =  orderPartService.getThingId(orderPart);
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

    public boolean contains(Order order, Thing thing) {
        for (OrderPart item : order.getOrderParts()) {
            if (orderPartService.getThingId(item).equals(thing.getId())) return true;
        }
        return false;
    }

    public OrderPart getOrderPartContain(Order order, Thing thing) {
        for (OrderPart orderPart : order.getOrderParts()) {
            if (orderPart.getThing().equals(thing)) return orderPart;
        }
        return null;

    }

    @Override
    public boolean isPartOfCompletedOrder(Thing thing) {
        if (thing == null) throw new ShopException("thing cannot be null");
        List<Order> orders = orderRepository.findAll();
        return orders.stream().anyMatch(order -> contains(order, thing));
    }

    @Override
    public void deleteOrderParts() {
        orderRepository.deleteAll();
    }
}
