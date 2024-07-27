package thkoeln.archilab.ecommerce.solution.client.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "CLIENT_TABLE")
@NoArgsConstructor
public class Client {
    @Id
    @Column(name = "client_id")
    private UUID clientId = UUID.randomUUID();

    @Embedded
    private Email email;
    @Embedded
    private HomeAddress homeAddress;
    private String name;

    @JoinColumn(name = "ORDER_TABLE")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Orderable> orderHistory = new ArrayList<>();

    public Client(String name, EmailType email, HomeAddressType homeAddress) {
        this.name = name;
        this.email = (Email) email;
        this.homeAddress = (HomeAddress) homeAddress;

    }

    public void addToOrderHistory(Orderable order) {
        orderHistory.add(order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}
