package tacos.taco;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/tacos", produces = "application/json")
@CrossOrigin(origins = {"http://tacocloud:8080", "http://localhost:8080"})
class TacoController {
    private final TacoRepository tacoRepository;

    @GetMapping(params = "recent")
    Iterable<Taco> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        return tacoRepository.findAll(page).getContent();
    }

    @GetMapping("/{id}")
    ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
        return tacoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    Taco postTaco(@RequestBody Taco taco) {
        return tacoRepository.save(taco);
    }
}
