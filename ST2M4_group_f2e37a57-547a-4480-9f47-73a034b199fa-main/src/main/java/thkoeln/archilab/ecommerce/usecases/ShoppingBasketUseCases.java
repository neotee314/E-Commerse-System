package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.Map;
import java.util.UUID;

/**
 * This interface contains methods needed in the context of use cases handling the shopping basket of
 * a client.
 */
public interface ShoppingBasketUseCases {
    /**
     * Adds a thing to the shopping basket of a client
     *
     * @param clientEmail
     * @param thingId
     * @param quantity
     * @throws ShopException if ...
     *      - the client with the given email does not exist,
     *      - the thing does not exist,
     *      - the quantity <= 0,
     *      - the thing is not available in the requested quantity
     */
    public void addThingToShoppingBasket( EmailType clientEmail, UUID thingId, int quantity );


    /**
     * Removes a thing from the shopping basket of a client
     *
     * @param clientEmail
     * @param thingId
     * @param quantity
     * @throws ShopException if ...
     *      - clientEmail is null,
     *      - the client with the given email does not exist,
     *      - the thing does not exist
     *      - the quantity <= 0,
     *      - the thing is not in the shopping basket in the requested quantity
     */
    public void removeThingFromShoppingBasket( EmailType clientEmail, UUID thingId, int quantity );


    /**
     * Returns a map showing which things are in the shopping basket of a client and how many of each thing
     *
     * @param clientEmail
     * @return the shopping basket of the client (map is empty if the shopping basket is empty)
     * @throws ShopException if ...
     *      - clientEmail is null,
     *      - the client with the given email does not exist
     */
    public Map<UUID, Integer> getShoppingBasketAsMap( EmailType clientEmail );


    /**
     * Returns the current value of all things in the shopping basket of a client
     *
     * @param clientEmail
     * @return the value of shopping basket of the client
     * @throws ShopException if ...
     *      - clientEmail is null,
     *      - the client with the given email does not exist
     */
    public MoneyType getShoppingBasketAsMoneyValue( EmailType clientEmail );


    /**
     * Get the number units of a specific thing that are currently reserved in the shopping baskets of all clients
     * @param thingId
     * @return the number of reserved things of that type in all shopping baskets
     * @throws ShopException
     *      - thingId is null
     *      - if the thing id does not exist
     */
    public int getReservedStockInShoppingBaskets( UUID thingId );


    /**
     * Checks if the shopping basket of a client is empty
     *
     * @param clientEmail
     * @return true if the shopping basket is empty, false otherwise
     * @throws ShopException if ...
     *    - clientEmail is null
     *    - the client with the given email does not exist
     */
    public boolean isEmpty( EmailType clientEmail );


    /**
     * Checks out the shopping basket of a client. This means that the things in the shopping basket
     * are removed from the stock. The shopping basket is emptied.
     *
     * @param clientEmail
     * @return the id of the order that was created
     * @throws ShopException if ...
     *      - clientEmail is null
     *      - the client with the given email does not exist
     *      - the shopping basket is empty
     *      - the things in the shopping basket are not available in the requested quantity
     */
    public UUID checkout( EmailType clientEmail );


    /**
     * Empties all shopping baskets for all clients
     */
    public void emptyAllShoppingBaskets();
}
