package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeliveryPackagePart {
    @Id
    private UUID id = UUID.randomUUID();;
    @ManyToOne
    private Thing thing;
    private int quantity;

    public DeliveryPackagePart(Thing thing, int quantity) {

        this.thing = thing;
        this.quantity = quantity;
    }
}
