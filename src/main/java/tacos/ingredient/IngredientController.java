package tacos.ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tacos.orders.models.Ingredient;

@RestController @RequiredArgsConstructor
@RequestMapping(path = "/api/ingredients", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class IngredientController {
    private final IngredientRepository ingredientRepository;

    @GetMapping
    Iterable<Ingredient> allIngredients() {
        return ingredientRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_writeIngredient')")
    @ResponseStatus(HttpStatus.CREATED)
    Ingredient saveIngredient(@RequestBody Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_deleteIngredient')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteIngredient(@PathVariable("id") String ingredientId) {
        ingredientRepository.deleteById(ingredientId);
    }
}
