package thkoeln.archilab.ecommerce.solution.stocklevel.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import java.util.List;
import java.util.UUID;
@Repository
public interface StockLevelRepository extends CrudRepository<StockLevel, UUID> {

}
