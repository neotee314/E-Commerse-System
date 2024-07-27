package thkoeln.archilab.ecommerce.solution.storageunit.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StorageUnitRepository extends CrudRepository<StorageUnit, UUID> {
}
