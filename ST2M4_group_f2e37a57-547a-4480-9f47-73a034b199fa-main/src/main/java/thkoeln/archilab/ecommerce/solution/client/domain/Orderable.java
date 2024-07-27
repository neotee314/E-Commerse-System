package thkoeln.archilab.ecommerce.solution.client.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public abstract class Orderable {
    @Id
    @Column(name = "order_table_id")
    protected UUID orderId = UUID.randomUUID();

}
