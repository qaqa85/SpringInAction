package tacos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import tacos.orders.models.Ingredient;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
class RestTemplateExampleTest {
    @LocalServerPort
    int serverPort;

    RestTemplate restTemplate = new RestTemplate();

    @Test
    void restTemplateTest() {
        // GIVEN
        Ingredient ingredient = new Ingredient("WRAP", "TACO", Ingredient.Type.WRAP);
        Ingredient underTest = createIngredient(ingredient);

        //  WHEN
        var result = getIngredientById("WRAP");

        assertThat(result.getName()).isEqualTo(ingredient.getName());
        assertThat(result.getType()).isEqualTo(ingredient.getType());
    }

    private Ingredient getIngredientById(String ingredientId) {
        return restTemplate.getForObject("http://localhost:" + serverPort + "/data-api/ingredients/{id}", Ingredient.class, ingredientId);
    }

    private Ingredient createIngredient(Ingredient ingredient) {
        return restTemplate.postForObject("http://localhost:" + serverPort + "/data-api/ingredients", ingredient, Ingredient.class);
    }
}