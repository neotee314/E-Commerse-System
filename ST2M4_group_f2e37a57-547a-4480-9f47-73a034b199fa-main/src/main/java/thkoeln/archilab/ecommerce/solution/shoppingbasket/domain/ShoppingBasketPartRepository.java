package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ShoppingBasketPartRepository extends CrudRepository<ShoppingBasketPart, UUID> {
}
