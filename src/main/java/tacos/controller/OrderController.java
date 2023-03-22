package tacos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.models.TacoOrder;
import tacos.repository.OrderRepository;

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
                               SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        orderRepository.save(order);

        log.info("Order submitted: {}", order);
        sessionStatus.setComplete();
        return "redirect:/";
    }
}
