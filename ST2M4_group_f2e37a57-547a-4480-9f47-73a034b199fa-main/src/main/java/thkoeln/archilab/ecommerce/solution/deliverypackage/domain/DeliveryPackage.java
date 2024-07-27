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

 //   @ElementCollection
 //   private Map<UUID, Integer> content = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<DeliveryPackagePart> parts = new ArrayList<>();

    //public DeliveryPackage(Map<UUID, Integer> deliveryContent) {
  //      this.content = deliveryContent;
//    }

    public void addPart(DeliveryPackagePart part) {
        parts.add(part);
    }

    public void remove(DeliveryPackagePart part) {
        parts.removeIf(deliveryPackagePart -> deliveryPackagePart.getThing().equals(part.getThing()));
    }
}
