package thkoeln.archilab.ecommerce.test.restdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

/**
 * A DTO containing just the id of an entity, used in testing.
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdDTO {
    private UUID id;
}

