package thkoeln.archilab.ecommerce.solution.client.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thkoeln.archilab.ecommerce.domainprimitives.Email;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends CrudRepository<Client, UUID> {
    Client findByEmail( Email email);
    List<Client> findAll();

}