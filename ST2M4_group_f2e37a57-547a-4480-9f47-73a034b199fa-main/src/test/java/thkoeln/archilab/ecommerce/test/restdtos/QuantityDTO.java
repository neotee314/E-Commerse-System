package thkoeln.archilab.ecommerce.test.restdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * A DTO containing just a quantity, used in testing.
 */


@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuantityDTO {
    private UUID thingId;
    private Integer quantity;

    public QuantityDTO( UUID thingId, Integer quantity ) {
        this.thingId = thingId;
        this.quantity = quantity;
    }
}

