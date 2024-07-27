package thkoeln.archilab.ecommerce.solution.client.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.client.domain.ClientTypeImp;
import thkoeln.archilab.ecommerce.usecases.ClientRegistrationUseCases;
import thkoeln.archilab.ecommerce.usecases.ClientType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

@Service
@RequiredArgsConstructor
public class ClientRegistrationService implements ClientRegistrationUseCases {

    private final ClientService clientService;

    @Override
    public void register(String name, EmailType email, HomeAddressType address) {
        if (name == null || name.isEmpty() || email == null || address == null) {
            throw new ShopException("Invalid data");
        }
        Client existingClient = clientService.retrieveClientByEmail(email);
        if (existingClient != null) {
            throw new ShopException("Client with email " + email.toString() + " already exists");
        }
        clientService.createClient(name, email, address);
    }

    @Override
    public void changeAddress(EmailType clientEmail, HomeAddressType address) {
        if (clientEmail==null) throw new ShopException("Email cannot be null");
        Client client = clientService.retrieveClientByEmail(clientEmail);
        if (client == null) {
            throw new ShopException("Client with email " + clientEmail + " does not exist");
        }
        if (address == null || address.getStreet() == null || address.getStreet().isEmpty() ||
                address.getCity() == null || address.getCity().isEmpty() ||
                address.getZipCode() == null) {
            throw new ShopException("Invalid data");
        }
        clientService.updateAddress(client, address);
    }

    @Override
    public ClientType getClientData(EmailType clientEmail) {
        if (clientEmail == null || clientEmail.toString().isEmpty()) {
            throw new ShopException("Invalid data");
        }
        Client client = clientService.retrieveClientByEmail(clientEmail);
        if (client == null) {
            throw new ShopException("Client with email " + clientEmail.toString() + " does not exist");
        }

        String name = client.getName();
        EmailType emailType = client.getEmail();
        HomeAddressType homeAddressType = client.getHomeAddress();
        return new ClientTypeImp(name,emailType,homeAddressType);
    }

    @Override
    public void deleteAllClients() {
        clientService.removeAllClients();
    }
}
