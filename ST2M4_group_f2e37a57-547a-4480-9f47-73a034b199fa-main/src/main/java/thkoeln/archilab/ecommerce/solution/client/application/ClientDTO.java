package thkoeln.archilab.ecommerce.solution.client.application;

import lombok.*;
import thkoeln.archilab.ecommerce.domainprimitives.Email;

import java.util.UUID;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private UUID id;
    private String name;
    private String emailString;
    private String city;
    private String zipCodeString;

}
