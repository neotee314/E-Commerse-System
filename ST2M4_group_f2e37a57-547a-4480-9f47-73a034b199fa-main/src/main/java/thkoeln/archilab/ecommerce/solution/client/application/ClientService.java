package thkoeln.archilab.ecommerce.solution.client.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.solution.client.domain.*;
import thkoeln.archilab.ecommerce.usecases.ClientType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;


    public void createClient(String name, EmailType email, HomeAddressType address) {
        Client client = new Client(name, email, address);
        clientRepository.save(client);
    }

    public void updateAddress(Client client, HomeAddressType newAddress) {
        client.setHomeAddress((HomeAddress) newAddress);
        clientRepository.save(client);
    }

    public Client findByEmail(EmailType clientEmail) {
        return clientRepository.findByEmail((Email) clientEmail);
    }

    public Client retrieveClientByEmail(EmailType email) {
        return clientRepository.findByEmail((Email) email);
    }

    public void removeAllClients() {
        clientRepository.deleteAll();
    }


    public List<Orderable> getOrderHistory(Client client) {
        if (client == null) throw new ShopException("client does not exist");
        return client.getOrderHistory();
    }

    public void addToOrderHistory(Client client, Orderable order) {
        if (client == null || order == null) throw new ShopException("invalid data");
        client.getOrderHistory().add(order);
        clientRepository.save(client);
    }

    public ClientType findClientByOrder(Orderable order) {
        List<Client> clients = clientRepository.findAll();
        for (Client client : clients) {
            List<Orderable> orderHistory = client.getOrderHistory();
            for (Orderable orderOfClient : orderHistory) {
                if (orderOfClient.getOrderId().equals(order.getOrderId())) {
                    return new ClientTypeImp(
                            client.getName(), client.getEmail(), client.getHomeAddress()
                    );
                }
            }
        }
        return null;
    }

    public ClientDTO findClientDTOByEmail(Email emailaddress) {
        Client client = findByEmail(emailaddress);
        if (client == null) return null;
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmailString(client.getEmail().getEmailAddress());
        clientDTO.setId(client.getClientId());
        clientDTO.setName(client.getName());
        clientDTO.setCity(client.getHomeAddress().getCity().toString());
        clientDTO.setZipCodeString(client.getHomeAddress().getZipCode().toString());
        return clientDTO;
    }

    public Client findClientById(UUID clientId) {
        if (clientId == null) throw new ShopException("id cannot be null");
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) return null;
        return client.get();
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }
}


