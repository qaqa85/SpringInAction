package tacos.orders.controllerMCV;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.orders.controllerMCV.configurator.OrderSizeProps;
import tacos.orders.exceptions.OrderNotFoundException;
import tacos.tacoOrder.TacoOrder;
import tacos.tacoOrder.TacoOrderRepository;
import tacos.security.user.models.User;

@Controller
@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "taco.orders")
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
class OrderController {
    private final TacoOrderRepository orderRepository;
    private final OrderSizeProps orderSizeProps;

    @GetMapping("/current")
    String orderForm() {
        return "orderForm";
    }

    @PostMapping
    String processOrder(@Valid TacoOrder order,
                        Errors errors,
                        SessionStatus sessionStatus,
                        @AuthenticationPrincipal User user
    ) {
        if (errors.hasErrors()) {
            return "orderForm";
        }
        order.setUser(user);
        orderRepository.save(order);

        log.info("Order submitted: {}", order);
        sessionStatus.setComplete();
        return "redirect:/orders";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    void deleteAllOrders() {
        orderRepository.deleteAll();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    String ordersFromUser(@AuthenticationPrincipal User user, Model model) {

        Pageable pageable = PageRequest.of(0, orderSizeProps.getPageSize());
        model.addAttribute("orders", orderRepository.findByUserOrderByPlacedAtDesc(user, pageable));
        model.addAttribute("username", user.getUsername());

        return "orderList";
    }
}
