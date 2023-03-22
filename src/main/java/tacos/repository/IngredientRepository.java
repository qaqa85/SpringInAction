package tacos.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tacos.models.Ingredient;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, String> {
}
