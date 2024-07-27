package thkoeln.archilab.ecommerce.test.restdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

/**
 * A DTO containing just the id of an entity, used in testing.
 */

@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageUnitIdDTO {
    private UUID storageUnitId;

    public UUID getStorageUnitId() {
        return storageUnitId;
    }
}

