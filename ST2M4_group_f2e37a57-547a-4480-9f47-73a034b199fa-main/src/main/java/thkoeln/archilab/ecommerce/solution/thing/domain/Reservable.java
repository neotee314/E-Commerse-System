package thkoeln.archilab.ecommerce.solution.thing.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Reservable {
    @Id
    protected UUID id = UUID.randomUUID();
}
