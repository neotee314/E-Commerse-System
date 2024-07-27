package thkoeln.archilab.ecommerce.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import thkoeln.archilab.ecommerce.test.restdtos.IdDTO;
import thkoeln.archilab.ecommerce.test.restdtos.QuantityDTO;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ShoppingBasketRESTHelper {
    private MockMvc mockMvc;
    private ThingCatalogUseCases thingCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;

    private static final ResultMatcher CREATED = status().isCreated();
    private static final ResultMatcher OK = status().isOk();
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;

    @Autowired
    public ShoppingBasketRESTHelper( MockMvc mockMvc,
                                    ThingCatalogUseCases thingCatalogUseCases,
                                    StorageUnitUseCases storageUnitUseCases ) {
        this.mockMvc = mockMvc;
        this.thingCatalogUseCases = thingCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases );
    }

    /**
     * Executes a GET query on a shopping basket and returns its id, with the client's email as a
     * starting point.
     * @param email
     * @return
     * @throws Exception
     */
    public UUID getQueryShoppingBasket( EmailType email ) throws Exception {
        return getQueryShoppingBasket( email, null );
    }


    /**
     * Executes a GET query on a shopping basket and returns its id, with the client's email as a
     * starting point.
     * @param email
     * @param quantityMap A map of things and their quantities in the shopping basket. (If null, then don't check.)
     * @return The id of the client's shopping basket.
     * @throws Exception
     */
    public UUID getQueryShoppingBasket( EmailType email, Map<UUID, Integer> quantityMap )
            throws Exception {
        // first query the clients API to get the proper clientId
        UUID clientId;
        String clientUri = "/clients?email=" + email.toString();
        MvcResult clientGetResult = mockMvc.perform( get( clientUri ) )
                .andExpect( status().isOk() )
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        IdDTO idDTO = objectMapper.readValue( clientGetResult.getResponse().getContentAsString(), IdDTO.class );
        clientId = idDTO.getId();

        // then query the shopping basket API and extract the shopping basket id
        String shoppingBasketUri = "/shoppingBaskets?clientId=" + clientId.toString();
        ResultActions resultActions = mockMvc.perform( get( shoppingBasketUri ) )
                .andExpect( status().isOk() );
        idDTO = objectMapper.readValue( resultActions.andReturn().getResponse().getContentAsString(), IdDTO.class );
        UUID shoppingBasketId = idDTO.getId();

        // additional checks, if desired
        if ( quantityMap != null ) {
            Float totalSalesPrice = thingAndStockMasterDataInitializer
                    .getTotalSalesPrice( quantityMap );
            String totalSalesPriceString = String.format( "%.2f €", totalSalesPrice );
            resultActions
                    .andExpect( jsonPath( "$.totalSalesPrice" ).value( totalSalesPriceString ) );
            checkQuantityMap( quantityMap, resultActions );
        }
        return shoppingBasketId;
    }


    public void checkQuantityMap( Map<UUID, Integer> quantityMap, ResultActions resultActions ) throws Exception {
        int numOfThings = quantityMap.size();
        resultActions.andExpect( jsonPath( "$.shoppingBasketParts.length()" ).value( numOfThings ) );
        for ( Map.Entry<UUID, Integer> entry : quantityMap.entrySet() ) {
            UUID thingId = entry.getKey();
            Integer quantity = entry.getValue();
            resultActions.andExpect( jsonPath( "$.shoppingBasketParts.[?(@.thingId == '" + thingId + "')].quantity" )
                    .value( quantity ) );
        }
    }


    /**
     * Adds a thing to a shopping basket.
     * @param shoppingBasketId Id of the shopping basket.
     * @param thingId Id of the thing.
     * @param quantity Quantity of the thing.
     * @throws Exception
     */
    public void addThingToShoppingBasket( UUID shoppingBasketId, UUID thingId, Integer quantity )
            throws Exception {
        addThingToShoppingBasket( shoppingBasketId, thingId, quantity, null );
    }


    /**
     * Adds a thing to a shopping basket.
     * @param shoppingBasketId Id of the shopping basket.
     * @param thingId Id of the thing.
     * @param quantity Quantity of the thing.
     * @param expectedStatus expected status of the operation (null = CREATED)
     * @throws Exception
     */
    public void addThingToShoppingBasket( UUID shoppingBasketId, UUID thingId, Integer quantity,
                                                  ResultMatcher expectedStatus ) throws Exception {
        ResultMatcher status = expectedStatus == null ? CREATED : expectedStatus;
        ObjectMapper objectMapper = new ObjectMapper();
        QuantityDTO dto = new QuantityDTO( thingId, quantity );
        String quantityJson = objectMapper.writeValueAsString( dto );
        mockMvc.perform( post( "/shoppingBaskets/" + shoppingBasketId + "/parts" )
                        .contentType( APPLICATION_JSON ).content( quantityJson ) )
                .andExpect( status );
    }


    /**
     * Deletes a thing from a shopping basket.
     * @param shoppingBasketId Id of the shopping basket.
     * @param thingId Id of the thing.
     * @throws Exception
     */
    public void deleteThingFromShoppingBasket( UUID shoppingBasketId, UUID thingId )
            throws Exception {
        deleteThingFromShoppingBasket( shoppingBasketId, thingId, null );
    }


    /**
     * Deletes a thing from a shopping basket.
     * @param shoppingBasketId Id of the shopping basket.
     * @param thingId Id of the thing.
     * @param expectedStatus expected status of the operation (null = OK)
     * @throws Exception
     */
    public void deleteThingFromShoppingBasket( UUID shoppingBasketId, UUID thingId,
                                                      ResultMatcher expectedStatus ) throws Exception {
        ResultMatcher status = expectedStatus == null ? OK : expectedStatus;
        String uri = "/shoppingBaskets/" + shoppingBasketId + "/parts/" + thingId;
        mockMvc.perform( delete( uri ) ).andExpect( status );
    }


    /**
     * Checks out a shopping basket, and returns the id of the created order.
     * @param shoppingBasketId
     * @return
     * @throws Exception
     */
    public UUID checkout( UUID shoppingBasketId ) throws Exception {
        return checkout( shoppingBasketId, null );
    }


    /**
     * Checks out a shopping basket, and returns the id of the created order.
     * @param shoppingBasketId
     * @param expectedStatus expected status of the operation (null = CREATED)
     * @return
     * @throws Exception
     */
    public UUID checkout( UUID shoppingBasketId, ResultMatcher expectedStatus ) throws Exception {
        ResultMatcher status = expectedStatus == null ? CREATED : expectedStatus;
        String uri = "/shoppingBaskets/" + shoppingBasketId + "/checkout";
        MvcResult result = mockMvc.perform( post( uri ) ).andExpect( status ).andReturn();
        if ( !status.equals( CREATED ) ) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        IdDTO idDTO = objectMapper.readValue( result.getResponse().getContentAsString(), IdDTO.class );
        return idDTO.getId();
    }
}
