package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.*;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SHOPKEEPING_TABLE")
public class ShoppingBasket {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ShoppingBasketPart> parts = new ArrayList<>();

    public ShoppingBasket(Client client) {
        this.client = client;
    }
}
