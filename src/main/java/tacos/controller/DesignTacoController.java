package tacos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.models.Ingredient;
import tacos.models.Ingredient.Type;
import tacos.models.Taco;
import tacos.models.TacoOrder;
import tacos.repository.IngredientRepository;

import java.util.Iterator;
import java.util.stream.StreamSupport;

@Slf4j
@SessionAttributes("tacoOrder")
@Controller
@RequestMapping("/design")
@RequiredArgsConstructor
class DesignTacoController {
    private final IngredientRepository ingredientRepository;

    @ModelAttribute
    void addIngredientsToModel(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
    }

    @ModelAttribute(name = "tacoOrder")
    TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco")
    Taco taco() {
        return new Taco();
    }

    @GetMapping
    String showDesignForm(@AuthenticationPrincipal OAuth2User principal) {
        return "design";
    }

    @PostMapping
    String processTaco(
            @Valid Taco taco,
            Errors errors,
            @ModelAttribute TacoOrder tacoOrder
    ) {
        if (errors.hasErrors()) {
            return "design";
        }

        tacoOrder.addTaco(taco);
        log.info("Processing taco: {}", taco);
        return "redirect:/orders/current";
    }

    private Iterable<Ingredient> filterByType(Iterable<Ingredient> ingredients, Type type) {
        Iterator<Ingredient> iterator = ingredients.iterator();

        return StreamSupport.stream(ingredients.spliterator(), false)
                .filter(ingredient -> ingredient.getType().equals(type))
                .toList();
    }
}
