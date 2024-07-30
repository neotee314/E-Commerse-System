package thkoeln.archilab.ecommerce.solution.client.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thkoeln.archilab.ecommerce.domainprimitives.Email;

@RestController
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/clients")
    public ResponseEntity<ClientDto> getClientByEmail(@RequestParam(required = false) String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        Email emailaddress = Email.of(email);
        ClientDto clientDTO = clientService.findClientDTOByEmail(emailaddress);
        if (clientDTO == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }

}
