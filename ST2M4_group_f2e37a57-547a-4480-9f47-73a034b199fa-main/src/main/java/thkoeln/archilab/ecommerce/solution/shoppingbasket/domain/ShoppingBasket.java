package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SHOPKEEPING_TABLE")
public class ShoppingBasket{
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ShoppingBasketPart> parts = new ArrayList<>();

    public ShoppingBasket(Client client) {
        this.client = client;
    }


    public boolean contains(Thing thing) {
        for (ShoppingBasketPart part : parts) {
            if (part.contains(thing)) {
                return true;
            }
        }
        return false;
    }

    public int getReservedQuantity(Thing thing) {
        int totalReserved = 0;
        for (ShoppingBasketPart part : parts) {
            if (part.contains(thing)) {
                totalReserved += part.getQuantity();
            }
        }
        return totalReserved;
    }

    public void addPart(ShoppingBasketPart newPart) {
        for (ShoppingBasketPart part : parts) {
            if (part.getThingId().equals(newPart.getThingId())) {
                part.addQuantity(newPart.getQuantity());
                return;
            }
        }
        ShoppingBasketPart o = new ShoppingBasketPart(newPart.getThing(), newPart.getQuantity());
        parts.add(o);
    }


    public boolean removeThingFromBasket(Thing thing, int quantity) {
        for (ShoppingBasketPart part : parts) {
            if (part.contains(thing) && part.getQuantity() >= quantity) {
                part.removeQuantity(quantity);
                if (part.getQuantity() == 0) {
                    parts.remove(part);
                }
                return true;
            }
        }
        return false;
    }


    public MoneyType getCartValue() {
        float totalValue = 0;
        for (ShoppingBasketPart part : parts) {
            totalValue += part.getSellingPrice().multiplyBy(part.getQuantity()).getAmount();
        }
        return Money.of(totalValue, "EUR");
    }

    public boolean isEmpty() {
        for (ShoppingBasketPart orderPart : parts) {
            if (orderPart.getQuantity() != 0) {
                return false;
            }
        }
        return true;
    }


    public Map<UUID, Integer> getAsMap() {
        Map<UUID, Integer> basketAsMap = new HashMap<>();
        for (ShoppingBasketPart orderPart : parts) {
            basketAsMap.put(orderPart.getThingId(), orderPart.getQuantity());
        }
        return basketAsMap;
    }


    public List<ShoppingBasketPart> getOrderPartsContaining(Thing thing) {
        List<ShoppingBasketPart> result = new ArrayList<>();
        for (ShoppingBasketPart orderPart : parts) {
            if (orderPart.contains(thing)) {
                result.add(orderPart);
            }
        }
        return result;
    }

    public void emptyBasket() {
        parts.clear();
    }
}
