package tacos.tacoOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tacos.orders.exceptions.OrderNotFoundException;
import tacos.tacoOrder.service.OrderMessagingService;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class TacoOrderController {
    private final TacoOrderRepository tacoOrderRepository;
    private final OrderMessagingService messagingService;

    @PostAuthorize("hasRole('ADMIN') or " +
            "returnObject.user.username == authentication.name")
    @GetMapping("/{orderId}")
    @ResponseBody
    TacoOrder getOrder(@PathVariable long orderId) {
        return tacoOrderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    @PutMapping(path = "/{orderId}", consumes = "application/json")
    TacoOrder putOrder(@PathVariable("orderId") Long orderId, @RequestBody TacoOrder order) {
        order.setId(orderId);
        return tacoOrderRepository.save(order);
    }

    @PatchMapping(path = "/{orderId}", consumes = "application/json")
    TacoOrder patchOrder(@PathVariable("orderId") Long orderId, @RequestBody TacoOrder patch) {
        TacoOrder tacoOrder = tacoOrderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        if(Objects.nonNull(patch.getDeliveryName())) {
            tacoOrder.setDeliveryName(patch.getDeliveryName());
        }
        if(Objects.nonNull(patch.getDeliveryStreet())) {
            tacoOrder.setDeliveryName(patch.getDeliveryStreet());
        }
        if(Objects.nonNull(patch.getDeliveryCity())) {
            tacoOrder.setDeliveryName(patch.getDeliveryCity());
        }
        if(Objects.nonNull(patch.getDeliveryState())) {
            tacoOrder.setDeliveryName(patch.getDeliveryState());
        }
        if(Objects.nonNull(patch.getDeliveryZip())) {
            tacoOrder.setDeliveryName(patch.getDeliveryZip());
        }
        if(Objects.nonNull(patch.getCcNumber())) {
            tacoOrder.setDeliveryName(patch.getCcNumber());
        }
        if(Objects.nonNull(patch.getCcExpiration())) {
            tacoOrder.setDeliveryName(patch.getCcExpiration());
        }
        if(Objects.nonNull(patch.getCcCVV())) {
            tacoOrder.setDeliveryName(patch.getCcCVV());
        }

        return tacoOrderRepository.save(tacoOrder);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            tacoOrderRepository.deleteById(orderId);
        } catch (EmptyResultDataAccessException ignored) {}
    }

    @PostMapping(consumes = "application/json")
    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.CREATED)
    TacoOrder postOrder(@RequestBody TacoOrder order) {
        messagingService.sendOrder(order);
        return tacoOrderRepository.save(order);
    }

    @PreAuthorize("permitAll")
    @GetMapping("/rabbitMq")
    TacoOrder getMessage() {
        return messagingService.receiveMessage();
    }
}
