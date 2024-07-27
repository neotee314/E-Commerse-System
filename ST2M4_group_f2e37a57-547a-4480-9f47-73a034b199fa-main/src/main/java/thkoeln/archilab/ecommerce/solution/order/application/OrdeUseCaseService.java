package thkoeln.archilab.ecommerce.solution.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderRepository;
import thkoeln.archilab.ecommerce.usecases.OrderUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdeUseCaseService implements OrderUseCases {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    public Map<UUID, Integer> getOrderHistory(EmailType clientEmail) {
        return orderService.getOrderHistory(clientEmail);
    }

    @Override
    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }
}
