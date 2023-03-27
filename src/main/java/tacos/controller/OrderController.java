package tacos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.exceptions.OrderNotFoundException;
import tacos.models.TacoOrder;
import tacos.repository.OrderRepository;
import tacos.security.user.models.User;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
class OrderController {
    private final OrderRepository orderRepository;

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
        return "redirect:/";
    }

    @PostAuthorize("hasRole('ADMIN') or " +
            "returnObject.user.username == authentication.name")
    @GetMapping("/{id}")
    @ResponseBody TacoOrder getOrder(@PathVariable long id, @AuthenticationPrincipal User user) {
        return orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    void deleteAllOrders() {
        orderRepository.deleteAll();
    }
}
