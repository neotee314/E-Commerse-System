package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.solution.order.application.OrderPartDTO;
import thkoeln.archilab.ecommerce.solution.order.application.OrderService;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.thing.application.ThingService;
import thkoeln.archilab.ecommerce.solution.thing.domain.Thing;
import thkoeln.archilab.ecommerce.usecases.ThingCatalogUseCases;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ShoppingBasketController {

    private final ShoppingBasketUseCaseService shoppingBasketUseCaseService;
    private final ShoppingBasketService shoppingBasketService;
    private final ThingService thingService;
    private final OrderService orderService;

    @GetMapping("/shoppingBaskets")
    public ResponseEntity<ShoppingBasketDTO> getShoppingBasketForClient(@RequestParam(value = "clientId", required = false) UUID clientId) {
        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        ShoppingBasketDTO shoppingBasketDTO = shoppingBasketService.findBasketByClientId(clientId);
        if (shoppingBasketDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(shoppingBasketDTO, HttpStatus.OK);
    }


    @PostMapping("/shoppingBaskets/{shoppingBasket-id}/parts")
    public ResponseEntity<?> addThingToShoppingBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId,
                                                      @RequestBody OrderPartDTO partDTO) {

        try {
            if (shoppingBasketId == null || partDTO.getThingId() == null)
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            if (partDTO.getQuantity() < 0) return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketId);
            if (shoppingBasket == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            Email clientEmail = shoppingBasket.getClient().getEmail();
            shoppingBasketUseCaseService.addThingToShoppingBasket(clientEmail, partDTO.getThingId(), partDTO.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ShopException e) {
            if (e.getMessage().equals("Thing is not available in the requested quantity"))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    //       (10) Delete a certain thing from the shopping basket

    @DeleteMapping("/shoppingBaskets/{shoppingBasket-id}/parts/{thing-id}")
    public ResponseEntity<?> deleteShoppingBasketPart(@PathVariable("shoppingBasket-id") UUID shoppingBasketId,
                                                      @PathVariable("thing-id") UUID thingId) {
        try {
            if (shoppingBasketId == null || thingId == null)
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketId);
            if (shoppingBasket == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            Email clientEmail = shoppingBasket.getClient().getEmail();

            Thing thing = thingService.findById(thingId);
            int currentlyReservedGood = shoppingBasket.getReservedQuantity(thing);
            shoppingBasketUseCaseService.removeThingFromShoppingBasket(clientEmail, thingId, currentlyReservedGood);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ShopException e) {
            if (e.getMessage().equals("Thing is not in the cart of client"))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


    }

    //     (11) Check out the shopping basket (and have the shop create an order as a consequence)

    @PostMapping("/shoppingBaskets/{shoppingBasket-id}/checkout")
    public ResponseEntity<IdDto> checkOutAShoppingBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId) {
        try {
            ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketId);
            UUID orderId = shoppingBasketUseCaseService.checkout(shoppingBasket.getClient().getEmail());
            Order order = orderService.findById(orderId);
            IdDto idDto = new IdDto();
            idDto.setId(orderId);

            return ResponseEntity.status(HttpStatus.CREATED).body(idDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
