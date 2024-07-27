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
    private UUID shoppingBasketPartId = UUID.randomUUID();

    private int quantity;


    @ManyToOne(cascade = CascadeType.PERSIST)
    private Thing thing;

    public ShoppingBasketPart(Thing thing, int reservedQuantity) {
        this.thing = thing;
        this.quantity = reservedQuantity;
    }

    public boolean contains(Thing thing) {
        return this.thing.getId().equals(thing.getId());
    }

    public UUID getThingId() {
        return thing.getId();
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void removeQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public Money getSellingPrice() {
        return this.thing.getSellingPrice();
    }

}