package thkoeln.archilab.ecommerce.solution.order.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderPart {

    @Id
    private UUID id =UUID.randomUUID();

    @ManyToOne
    private Thing thing;

    private int orderQuantity;


    public OrderPart(Thing thing, int quantity) {
        this.thing = thing;
        this.orderQuantity = quantity;
    }

    public void addQuantity(int quantity) {
        orderQuantity += quantity;
    }

    public boolean contains(Thing thing) {
        return this.thing.equals(thing);
    }

    public UUID getThingId() {
        return this.thing.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderPart)) return false;
        OrderPart orderPart = (OrderPart) o;
        return Objects.equals(thing, orderPart.getThing());
    }

}
