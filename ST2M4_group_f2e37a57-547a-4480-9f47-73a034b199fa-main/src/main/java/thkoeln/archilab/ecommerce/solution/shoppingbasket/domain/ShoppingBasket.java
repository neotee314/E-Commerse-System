package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.thing.domain.Reservable;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SecondaryTable(name = "SHOPKEEPING_TABLE")
public class ShoppingBasket extends Reservable {

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ShoppingBasketPart> parts = new ArrayList<>();

    public ShoppingBasket(Client client) {
        this.client = client;
    }

    public UUID getId(){
        return super.id;
    }

}
