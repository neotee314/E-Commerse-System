package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShoppingBasketRepository extends CrudRepository<ShoppingBasket, UUID> {
    List<ShoppingBasket> findAll();
    ShoppingBasket findByClient(Client client);

}


