package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

/**
 * This interface contains methods needed in the context of the registration of a client.
 */
public interface ClientRegistrationUseCases {
    /**
     * Registers a new client
     *
     * @param name
     * @param email
     * @param homeAddress
     * @throws ShopException if ...
     *      - name is null or empty
     *      - email is null
     *      - client with the given email already exists
     *      - homeAddress is null
     */
    public void register( String name, EmailType email, HomeAddressType homeAddress );


    /**
     * Changes the homeAddress of a client
     *
     * @param clientEmail
     * @param homeAddress
     * @throws ShopException if ...
     *      - email is null
     *      - client with the given email does not exist
     *      - homeAddress is null
     */
    public void changeAddress( EmailType clientEmail, HomeAddressType homeAddress );


    /**
     * Returns the data of a client (by an object implementing ClientType)
     * @param clientEmail
     * @return the client data
     * @throws ShopException if ...
     *      - email is null
     *      - the client with the given email does not exist
     */
    public ClientType getClientData( EmailType clientEmail );



    /**
     * Clears all clients, including all orders and shopping baskets
     */
    public void deleteAllClients();
}
