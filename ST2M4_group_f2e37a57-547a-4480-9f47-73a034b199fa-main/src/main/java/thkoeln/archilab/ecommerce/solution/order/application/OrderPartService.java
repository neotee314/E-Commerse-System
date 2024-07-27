package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPartRepository;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;


@Service
@RequiredArgsConstructor
public class OrderPartService {

    private final OrderPartRepository orderPartRepository;


    public OrderPart createOrderPart(Thing thing, int quantity) {
        OrderPart orderPart = new OrderPart(thing, quantity);
        orderPartRepository.save(orderPart);
        return orderPart;
    }

}
