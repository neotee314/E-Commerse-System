package thkoeln.archilab.ecommerce.test.shoppingbasket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import thkoeln.archilab.ecommerce.solution.client.application.ClientService;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.application.ShoppingBasketManagementService;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.test.ShoppingBasketRESTHelper;
import thkoeln.archilab.ecommerce.usecases.ShoppingBasketUseCases;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.ClientRegistrationUseCases;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer;
import thkoeln.archilab.ecommerce.usecases.masterdata.Purgatory;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ThingAndStockMasterDataInitializer.*;
import static thkoeln.archilab.ecommerce.usecases.masterdata.ClientMasterDataInitializer.CLIENT_EMAIL;
import static thkoeln.archilab.ecommerce.usecases.masterdata.FactoryMethodInvoker.instantiateEmail;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShoppingBasketRESTTest {
    @Autowired
    private ClientRegistrationUseCases clientRegistrationUseCases;
    @Autowired
    private ThingCatalogUseCases thingCatalogUseCases;
    @Autowired
    private StorageUnitUseCases storageUnitUseCases;
    @Autowired
    private ShoppingBasketUseCases shoppingBasketUseCases;
    @Autowired
    private Purgatory purgatory;
    @Autowired
    private MockMvc mockMvc;

    private ShoppingBasketRESTHelper shoppingBasketRESTHelper;
    private ClientMasterDataInitializer clientMasterDataInitializer;
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;
    private EmailType nonExistingEmail;

    private static final ResultMatcher NOT_FOUND = status().isNotFound();
    private static final ResultMatcher OK = status().isOk();
    private static final ResultMatcher CREATED = status().isCreated();
    private static final ResultMatcher UNPROCESSABLE_ENTITY = status().isUnprocessableEntity();
    private static final ResultMatcher CONFLICT = status().isConflict();

    @BeforeEach
    public void setUp() {
        shoppingBasketRESTHelper = new ShoppingBasketRESTHelper(
                mockMvc, thingCatalogUseCases, storageUnitUseCases);
        purgatory.deleteEverything();

        clientMasterDataInitializer = new ClientMasterDataInitializer(clientRegistrationUseCases);
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                thingCatalogUseCases, storageUnitUseCases);
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();

        nonExistingEmail = instantiateEmail("harry@sally.de");
    }


    @Test
    public void testInvalidShoppingBasketBaseUris() throws Exception {
        // given
        String allShoppingBasketsUri = "/shoppingBaskets";
        String randomIdUri = "/shoppingBaskets/" + UUID.randomUUID();
        String randomClientUUID = "/shoppingBaskets?clientId=" + UUID.randomUUID();
        String invalidClientUUID = "/shoppingCarts?customerId=" + "invalidUUID";
        String randomParamUri = "/shoppingBaskets?randomParam=randomValue";

        // when
        // then
        mockMvc.perform(get(allShoppingBasketsUri)).andExpect(status().isMethodNotAllowed());
        mockMvc.perform(get(randomIdUri)).andExpect(status().isNotFound());
        mockMvc.perform(get(randomClientUUID)).andExpect(status().isNotFound());
        mockMvc.perform(get(invalidClientUUID)).andExpect(status().isNotFound());
        mockMvc.perform(get(randomParamUri)).andExpect(status().is4xxClientError());
    }


    @Test
    public void testInvalidShoppingBasketPartUris() throws Exception {
        // given
        EmailType clientEmail8 = CLIENT_EMAIL[8];
        UUID shoppingBasketId8 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail8);
        String randomShoppingBasketPartUri = "/shoppingBaskets/" + shoppingBasketId8.toString() + "/random";
        String randomThingUri = "/shoppingBaskets/" + shoppingBasketId8.toString()
                + "/parts/" + UUID.randomUUID();

        // when
        // then
        mockMvc.perform(post(randomShoppingBasketPartUri)).andExpect(status().isNotFound());
        mockMvc.perform(delete(randomThingUri)).andExpect(status().isNotFound());
    }


    @Test
    public void testInvalidAddToShoppingBasket() throws Exception {
        // given
        UUID nonExistentThingId = UUID.randomUUID();
        UUID thingId5 = (UUID) THING_DATA[5][0];
        UUID thingId0 = (UUID) THING_DATA[0][0];
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        EmailType clientEmail0 = CLIENT_EMAIL[0];
        Map<UUID, Integer> startQuantityMap = new HashMap<>();
        startQuantityMap.put(thingId2, 19);

        // when
        UUID shoppingBasketId = shoppingBasketRESTHelper.getQueryShoppingBasket(
                clientEmail0, new HashMap<>());
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId, thingId2, 6);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId, thingId2, 13);

        // then
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail0, startQuantityMap);

        // ... and check a couple of invalid cases
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId, nonExistentThingId, 12, NOT_FOUND);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                UUID.randomUUID(), thingId5, 12, NOT_FOUND);
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId, thingId5, -1, UNPROCESSABLE_ENTITY);

        // thingId0 is not available in the requested quantity (or rather, not at all)
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId, thingId0, 1, CONFLICT);

        // There are only 10 pieces of thingId1 available
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId, thingId1, 11, CONFLICT);

        // There are only 20 pieces of thingId1 available - we have already 19 in the shopping basket
        shoppingBasketRESTHelper.addThingToShoppingBasket(
                shoppingBasketId, thingId2, 2, CONFLICT);

        // check if the shopping basket is still the same
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail0, startQuantityMap);
    }

    @Autowired
    private ShoppingBasketManagementService shoppingBasketService;
    @Autowired
    private ClientService clientService;

    @Test
    public void testDeleteThingFromShoppingBasket() throws Exception {
        // given
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        UUID thingId3 = (UUID) THING_DATA[3][0];
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        UUID shoppingBasketId6 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);

        Map<UUID, Integer> quantityMap1 = new HashMap<>();
        quantityMap1.put(thingId1, 1);
        quantityMap1.put(thingId2, 2);
        quantityMap1.put(thingId3, 3);
        Map<UUID, Integer> quantityMap2 = new HashMap<>();
        quantityMap2.put(thingId1, 1);
        quantityMap2.put(thingId3, 3);

        // when
        // then
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId6, thingId1, 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId6, thingId2, 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId6, thingId3, 3);
        ShoppingBasket shoppingBasket1 = shoppingBasketService.findById(shoppingBasketId6);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6, quantityMap1);

        shoppingBasketRESTHelper.deleteThingFromShoppingBasket(shoppingBasketId6, thingId2);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6, quantityMap2);
    }


    @Test
    public void testNoDoubleDelete() throws Exception {
        // given
        UUID thingId3 = (UUID) THING_DATA[3][0];
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        UUID shoppingBasketId6 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);

        // when
        // then
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId6, thingId3, 3);
        shoppingBasketRESTHelper.deleteThingFromShoppingBasket(shoppingBasketId6, thingId3);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6, new HashMap<>());
        shoppingBasketRESTHelper.deleteThingFromShoppingBasket(shoppingBasketId6, thingId3, CONFLICT);
    }


    @Test
    public void testAddRemoveThingsFromAndToShoppingBasket() throws Exception {
        // given
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        EmailType clientEmail5 = CLIENT_EMAIL[5];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);
        UUID shoppingBasketId5 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail5);

        // when
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId1, 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId2, 3);
        shoppingBasketRESTHelper.deleteThingFromShoppingBasket(shoppingBasketId3, thingId1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId1, 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId2, 6);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId5, thingId1, 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId5, thingId2, 8);
        shoppingBasketRESTHelper.deleteThingFromShoppingBasket(shoppingBasketId5, thingId1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId5, thingId1, 1);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId5, thingId2, 2);

        int reservedStock1 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId1);
        int reservedStock2 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId2);

        // then
        // client3 has 1x thingId1 and 9x thingId2 in cart
        Map<UUID, Integer> cart3 = new HashMap<>();
        cart3.put(thingId1, 1);
        cart3.put(thingId2, 9);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3, cart3);

        // client5 has 1x thingId1 and 10x thingId2 in cart
        Map<UUID, Integer> cart5 = new HashMap<>();
        cart5.put(thingId1, 1);
        cart5.put(thingId2, 10);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail5, cart5);

        assertEquals(2, reservedStock1);
        assertEquals(19, reservedStock2);
    }


    @Test
    public void testImpactOfStockCorrectionToOneShoppingBasket() throws Exception {
        // given
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        UUID thingId3 = (UUID) THING_DATA[3][0];
        UUID storageUnitId0 = STORAGE_UNIT_ID[0];
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);

        // when
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId1, 6);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId2, 15);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId3, 1);
        storageUnitUseCases.changeStockTo(storageUnitId0, thingId1, 4);
        storageUnitUseCases.changeStockTo(storageUnitId0, thingId2, 16);
        storageUnitUseCases.changeStockTo(storageUnitId0, thingId3, 0);
        int reservedStock1 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId1);
        int reservedStock2 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId2);
        int reservedStock3 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId3);

        ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketId3);

        // then
        Map<UUID, Integer> cart3 = new HashMap<>();
        cart3.put(thingId1, 4);
        cart3.put(thingId2, 15);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3, cart3);

        assertEquals(4, reservedStock1);
        assertEquals(15, reservedStock2);
        assertEquals(0, reservedStock3);
    }


    @Test
    public void testImpactOfStockCorrectionToSeveralShoppingBaskets() throws Exception {
        // given
        UUID thingId2 = (UUID) THING_DATA[2][0];
        EmailType clientEmail3 = CLIENT_EMAIL[3];
        EmailType clientEmail6 = CLIENT_EMAIL[6];
        EmailType clientEmail9 = CLIENT_EMAIL[9];
        UUID shoppingBasketId3 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3);
        UUID shoppingBasketId6 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6);
        UUID shoppingBasketId9 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail9);
        UUID storageUnitId0 = STORAGE_UNIT_ID[0];

        // when
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId3, thingId2, 3);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId6, thingId2, 6);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId9, thingId2, 9);
        storageUnitUseCases.removeFromStock(storageUnitId0, thingId2, 2);

        int reservedStock21 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId2);
        Map<UUID, Integer> cart3 = new HashMap<>();
        cart3.put(thingId2, 3);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail3, cart3);
        Map<UUID, Integer> cart6 = new HashMap<>();
        cart6.put(thingId2, 6);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail6, cart6);
        Map<UUID, Integer> cart9 = new HashMap<>();
        cart9.put(thingId2, 9);
        shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail9, cart9);

        // for simplicity reasons, we use the use case interface here
        storageUnitUseCases.removeFromStock(storageUnitId0, thingId2, 8);
        Map<UUID, Integer> cart32 = shoppingBasketUseCases.getShoppingBasketAsMap(clientEmail3);
        Map<UUID, Integer> cart62 = shoppingBasketUseCases.getShoppingBasketAsMap(clientEmail6);
        Map<UUID, Integer> cart92 = shoppingBasketUseCases.getShoppingBasketAsMap(clientEmail9);
        int quantity32 = cart32.get(thingId2) == null ? 0 : cart32.get(thingId2);
        int quantity62 = cart62.get(thingId2) == null ? 0 : cart62.get(thingId2);
        int quantity92 = cart92.get(thingId2) == null ? 0 : cart92.get(thingId2);
        int reservedStock22 = shoppingBasketUseCases.getReservedStockInShoppingBaskets(thingId2);

        // then
        assertEquals(18, reservedStock21);
        assertEquals(10, reservedStock22);
        assertEquals(reservedStock22, quantity32 + quantity62 + quantity92);
    }


    @Test
    public void testCheckoutCreatesOrder() throws Exception {
        // given
        UUID thingId1 = (UUID) THING_DATA[1][0];
        UUID thingId2 = (UUID) THING_DATA[2][0];
        EmailType clientEmail9 = CLIENT_EMAIL[9];
        UUID shoppingBasketId9 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail9);
        int stock1before = THING_STOCK.get(thingId1)[0];
        int stock2before = THING_STOCK.get(thingId2)[0];

        // when
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId9, thingId1, 2);
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId9, thingId2, 3);

        UUID orderId = shoppingBasketRESTHelper.checkout(shoppingBasketId9);
        int stock1after = storageUnitUseCases.getAvailableStock(thingId1);
        int stock2after = storageUnitUseCases.getAvailableStock(thingId2);

        // then
        assertNotNull(orderId);
        assertEquals(2, stock1before - stock1after);
        assertEquals(3, stock2before - stock2after);
    }


    @Test
    public void testNoDoubleCheckout() throws Exception {
        // given
        EmailType clientEmail9 = CLIENT_EMAIL[9];
        UUID shoppingBasketId9 = shoppingBasketRESTHelper.getQueryShoppingBasket(clientEmail9);
        UUID thingId2 = (UUID) THING_DATA[2][0];

        // when
        shoppingBasketRESTHelper.addThingToShoppingBasket(shoppingBasketId9, thingId2, 3);

        // then
        shoppingBasketRESTHelper.checkout(shoppingBasketId9, CREATED);
        shoppingBasketRESTHelper.checkout(shoppingBasketId9, CONFLICT);
    }

}
