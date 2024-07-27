package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.UUID;


/**
 * This interface contains methods needed in the context of use cases concerning the thing catalog.
 */

public interface ThingCatalogUseCases {
    /**
     * Adds a new thing to the shop catalog
     * @param thingId
     * @param name
     * @param description
     * @param size
     * @param buyingPrice - the price the shop pays for the thing
     * @param salesPrice
     * @throws ShopException if ...
     *      - thingId is null,
     *      - the thing with that id already exists,
     *      - name or description are null or empty,
     *      - the size is <= 0 (but can be null!),
     *      - the buyingPrice is null,
     *      - the sales price is null,
     *      - the sales price is lower than the buyingPrice
     */
    public void addThingToCatalog( UUID thingId, String name, String description, Float size,
                                           MoneyType buyingPrice, MoneyType salesPrice );



    /**
     * Removes a thing from the shop catalog
     * @param thingId
     * @throws ShopException if
     *      - the thing id does not exist
     *      - the thing is still in stock
     *      - the thing is still reserved in a shopping basket, or part of a completed order
     */
    public void removeThingFromCatalog( UUID thingId );


    /**
     * Get the sales price of a given thing
     * @param thingId
     * @return the sales price
     * @throws ShopException if ...
     *      - thingId is null,
     *      - the thing with that id does not exist
     */
    public MoneyType getSalesPrice( UUID thingId );


    /**
     * Clears the thing catalog, i.e. removes all things from the catalog, including all references in
     * stocks, all the reservations in shopping baskets and all the orders. Intended for testing purposes.
     */
    public void deleteThingCatalog();

}
