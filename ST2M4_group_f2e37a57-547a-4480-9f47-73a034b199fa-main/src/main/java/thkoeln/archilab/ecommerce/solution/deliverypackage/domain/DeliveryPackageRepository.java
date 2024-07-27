package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;

import java.util.UUID;

@Repository
public interface DeliveryPackageRepository extends CrudRepository<DeliveryPackage, UUID> {
}
