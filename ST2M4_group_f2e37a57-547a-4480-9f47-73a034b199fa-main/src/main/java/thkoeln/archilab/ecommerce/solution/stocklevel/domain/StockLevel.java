package thkoeln.archilab.ecommerce.solution.stocklevel.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockLevel {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    private Thing thing;

    private int quantity;

    public StockLevel(Thing thing, int quantity) {
        this.thing = thing;
        this.quantity = quantity;
    }
}
