package tacos;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;

public class reactorTests {
    @Test
    void simpleSubscribe() {
        createAFluxJust().subscribe(f -> System.out.println("Here's some fruit: " + f));
    }

    @Test
    void fruitFluxTest() {
        StepVerifier.create(createAFluxJust())
                .expectNext("Apple", "Orange", "Grape", "Banana", "Strawberry")
                .verifyComplete();
    }

    @Test
    void fluxCounter() {
        Flux<Integer> counter = Flux.range(1, 5);

        StepVerifier.create(counter)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    void intervalFlux() {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)).take(5);

        StepVerifier.create(interval)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    void mergeFlux() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500));
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);

        StepVerifier.create(mergedFlux)
                .expectNext("Garfield")
                .expectNext("Lasagna")
                .expectNext("Kojak")
                .expectNext("Lollipops")
                .expectNext("Barbossa")
                .expectNext("Apples")
                .verifyComplete();
    }

    @Test
    void zipTupleFlux() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);

        StepVerifier.create(zippedFlux)
                .expectNextMatches(p ->
                        p.getT1().equals("Garfield") &&
                                p.getT2().equals("Lasagna"))
                .expectNextMatches(p ->
                        p.getT1().equals("Kojak") &&
                                p.getT2().equals("Lollipops"))
                .expectNextMatches(p ->
                        p.getT1().equals("Barbossa") &&
                                p.getT2().equals("Apples"))
                .verifyComplete();
    }

    @Test
    void zipFunctionFlux() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

        Flux<String> zippedFlux = Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);

        StepVerifier.create(zippedFlux)
                .expectNext("Garfield eats Lasagna")
                .expectNext("Kojak eats Lollipops")
                .expectNext("Barbossa eats Apples")
                .verifyComplete();
    }

    @Test
    public void firstWithSignalFlux() {
        Flux<String> slowStartFlux = Flux.just("tortoise", "snail", "sloth")
                .delaySubscription(Duration.ofMillis(100));
        Flux<String> fastStartFlux = Flux.just("here", "cheetah", "squirrel")
                .delayElements(Duration.ofMillis(30));

        Flux<String> firstFlux = Flux.firstWithSignal(slowStartFlux, fastStartFlux);

        StepVerifier.create(firstFlux)
                .expectNext("here")
                .expectNext("cheetah")
                .expectNext("squirrel")
                .verifyComplete();
    }

    private Flux<String> createAFluxJust() {
        return Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
    }
}
