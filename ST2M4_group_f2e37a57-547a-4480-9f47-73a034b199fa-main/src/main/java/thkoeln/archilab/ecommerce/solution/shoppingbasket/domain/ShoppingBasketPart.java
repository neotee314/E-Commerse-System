package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ShoppingBasketPart {
    @Id
    private UUID id = UUID.randomUUID();

    private int quantity;


    @ManyToOne(cascade = CascadeType.PERSIST)
    private Thing thing;

    public ShoppingBasketPart(Thing thing, int reservedQuantity) {
        this.thing = thing;
        this.quantity = reservedQuantity;
    }



}