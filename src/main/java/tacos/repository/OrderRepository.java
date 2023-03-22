package tacos.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tacos.models.TacoOrder;

@Repository
public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
}
