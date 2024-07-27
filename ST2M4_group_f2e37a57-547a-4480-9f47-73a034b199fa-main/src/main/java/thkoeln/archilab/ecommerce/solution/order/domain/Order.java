package thkoeln.archilab.ecommerce.solution.order.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.client.domain.Orderable;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@SecondaryTable(name = "ORDER_TABLE")
public class Order extends Orderable {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderPart> orderParts = new ArrayList<>();

    private LocalDate orderDate;

    public Order() {
        orderDate = LocalDate.now();
    }

    public void addOrderPart(OrderPart orderPart) {
        for (OrderPart o : orderParts) {
            if (o == orderPart) {
                o.addQuantity(orderPart.getOrderQuantity());
                return;
            }
        }
        orderParts.add(orderPart);
    }


    public int getQuantity() {
        int quantity = 0;
        for (OrderPart orderPart : orderParts) {
            quantity += orderPart.getOrderQuantity();
        }
        return quantity;
    }

    public boolean contains(Thing thing) {
        for (OrderPart item : orderParts) {
            if (item.getThingId().equals(thing.getId())) return true;
        }
        return false;
    }

    public OrderPart getOrderPartContain(Thing thing) {
        for (OrderPart orderPart : orderParts) {
            if (orderPart.getThing().equals(thing)) return orderPart;
        }
        return null;

    }


}
