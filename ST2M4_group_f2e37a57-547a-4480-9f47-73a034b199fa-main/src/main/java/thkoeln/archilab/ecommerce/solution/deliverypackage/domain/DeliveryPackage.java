package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeliveryPackage {
    @Id
    private UUID deliveryId = UUID.randomUUID();

    @OneToMany(cascade = CascadeType.ALL)
    private List<DeliveryPackagePart> parts = new ArrayList<>();

}
