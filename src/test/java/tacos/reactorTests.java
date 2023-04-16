package tacos;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class reactorTests {

    public static final List<String> FRUITS = List.of("Apple", "Orange", "Grape", "Banana", "Strawberry");

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

    @Test
    void skipAFew() {
        Flux<String> skippedFlux = createAFluxJust().skip(3);

        StepVerifier.create(skippedFlux)
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    @Test
    void skipWithDelay() {
        Flux<String> skippedFlux = createAFluxJust().delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(4));

        StepVerifier.create(skippedFlux)
                .expectNext("Banana")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    @Test
    void takeAFew() {
        Flux<String> takeFlux = createAFluxJust()
                .take(2);

        StepVerifier.create(takeFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .verifyComplete();
    }

    @Test
    void takeWithDelay() {
        Flux<String> takeFlux = createAFluxJust().delayElements(Duration.ofSeconds(1))
                .take(Duration.ofMillis(3500));

        StepVerifier.create(takeFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .verifyComplete();
    }

    @Test
    void filter() {
        Flux<String> takeFlux = createAFluxJust()
                .filter(fruit -> fruit.endsWith("e"));

        StepVerifier.create(takeFlux)
                .expectNext("Apple")
                .expectNext("Orange")
                .expectNext("Grape")
                .verifyComplete();
    }

    @Test
    void distinct() {
        Flux<String> distinctFlux = Flux.just("one", "two", "three", "one", "one")
                .distinct();

        StepVerifier.create(distinctFlux)
                .expectNext("one")
                .expectNext("two")
                .expectNext("three")
                .verifyComplete();
    }

    @Test
    void mapSynchronous() {
        Flux<Integer> fruitsLength = createAFluxJust()
                .take(2)
                .map(String::length);

        StepVerifier.create(fruitsLength)
                .expectNext(5)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    void flatMapAsynchronous() {
        Flux<Integer> fruitsLength = createAFluxJust()
                .take(2)
                .flatMap(fruit -> Mono.just(fruit)
                        .map(String::length)
                        .subscribeOn(Schedulers.parallel())
                );

        StepVerifier.create(fruitsLength)
                .expectNext(5)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    void buffer() {
        Flux<List<String>> bufferFruit = createAFluxJust()
                .buffer(3);

        StepVerifier.create(bufferFruit)
                .expectNext(List.of("Apple", "Orange", "Grape"))
                .expectNext(List.of("Banana", "Strawberry"))
                .verifyComplete();
    }

    @Test
    void bufferAndFlatMap() {
        createAFluxJust()
                .buffer(3)
                .flatMap(fruits -> Flux.fromIterable(fruits)
                        .map(String::toUpperCase)
                        .subscribeOn(Schedulers.parallel())
                        .log()
                ).subscribe();
    }

    @Test
    void collect() {
        Mono<List<String>> collect = createAFluxJust().collectList();

        StepVerifier.create(collect)
                .expectNext(FRUITS)
                .verifyComplete();
    }

    @Test
    void collectToMap() {
        Mono<Map<Character, String>> fruitMap = createAFluxJust()
                .collectMap(fruit -> fruit.charAt(0));

        StepVerifier.create(fruitMap)
                .expectNextMatches(map ->
                        map.size() == 5 &&
                                map.get('A').equals("Apple") &&
                                map.get('O').equals("Orange") &&
                                map.get('G').equals("Grape") &&
                                map.get('B').equals("Banana") &&
                                map.get('S').equals("Strawberry"))
                .verifyComplete();
    }

    @Test
    public void all() {
        Mono<Boolean> hasAorEMono = createAFluxJust()
                .all(fruit -> fruit.matches(".*[ae].*"));

        StepVerifier.create(hasAorEMono)
                .expectNext(true)
                .verifyComplete();

        Mono<Boolean> hasZMono = createAFluxJust()
                .all(fruit -> fruit.contains("z"));

        StepVerifier.create(hasZMono)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void any() {
        Mono<Boolean> trueFruitMono = createAFluxJust()
                .any("Banana"::equals);

        StepVerifier.create(trueFruitMono)
                .expectNext(true)
                .verifyComplete();

        Mono<Boolean> falseFruitMono = createAFluxJust()
                .any("Pineapple"::equals);

        StepVerifier.create(falseFruitMono)
                .expectNext(false)
                .verifyComplete();
    }




    private Flux<String> createAFluxJust() {
        return Flux.fromIterable(FRUITS);
    }
}
