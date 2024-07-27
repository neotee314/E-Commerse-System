package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryPackagePartRepository extends CrudRepository<DeliveryPackagePart, UUID> {
}
