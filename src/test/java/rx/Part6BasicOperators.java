package rx;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;
import static rx.Helpers.randomLong;
import static rx.Helpers.sleep;

public class Part6BasicOperators {
    @Test
    public void map() throws Exception {
        Observable.just("1", "3").map(event -> event + "suffix").subscribe(Helpers::log);
        Observable.just(1, 3).map(event -> event + 1).map(event -> Integer.toString(event)).subscribe(Helpers::log);
    }

    @Test
    public void filter() throws Exception {
        // if possible, always do map after filter
        Observable.range(1, 10).filter(i -> i % 3 == 0).map(event -> Integer.toString(event)).subscribe(Helpers::log);
    }

    @Test
    public void doOnNextAndLimit() throws Exception {
        Observable.range(1, 10)
                .doOnNext(Helpers::log)
                .doOnUnsubscribe(() -> log("Unsubscribe"))
                .limit(3) // do unsubscribe after reaching limit
                .subscribe(Helpers::log, err -> {}, () -> log("Complete"));
    }

    @Test
    public void flatMap() throws Exception {
        log("Cartesian product");
        Observable.just("a", "b").flatMap(x -> Observable.just(x + "c", x+"d")).subscribe(Helpers::log);

        log("flatMap as map");
        Observable.just(1, 2, 3).flatMap(x -> Observable.just(x * 2)).subscribe(Helpers::log);

        log("flatMap as filter");
        Observable.just(1, 2, 3).flatMap(x -> x % 2 == 0 ? Observable.empty() : Observable.just(x)).subscribe(Helpers::log);
    }

    @Test
    public void extendedFlatMap() throws Exception {
        log("also works on erros and on completes");
        Observable.just(1)
                .flatMap(x -> Observable.just(x, x), err -> Observable.empty(), () -> Observable.just(3))
                .subscribe(Helpers::log);
    }

    @Test
    public void flatMapCompletable() throws Exception {
        Observable.just(1, 2)
                .flatMapCompletable(i -> Completable.complete() /* could be external call */)
                .subscribe(
                        x -> Helpers.log("Task completed: " + x), // will not be emitted
                        err -> {},
                        () -> Helpers.log("Only completed") // after all inner observables are completed
                );
    }

    @Test
    public void flatMapIterable() throws Exception {
        Observable.just(1, 2).flatMapIterable(x -> Arrays.asList(x*2, x*3)).subscribe(Helpers::log);
    }

    @Test
    public void flatMapOrderPreservation() throws Exception {
        log("sleep sort");
        Observable.just(10, 1, 8, 3, 2, 7, 8, 9)
                .flatMap(x -> Observable.just(x).delay(x*10, TimeUnit.MILLISECONDS))
                .subscribe(Helpers::log);

        sleep(300);

        log("chaos");
        Observable.just(10, 8, 3, 2, 7, 8, 9)
                .flatMap(x -> Observable.just(x, x*10)
                        .delay(y -> Observable.just(0).delay(randomLong(100), TimeUnit.MILLISECONDS)))
                .subscribe(Helpers::log);

        sleep(200);
    }

    @Test
    public void concatMap() throws Exception {
        Observable.just(10, 8, 3, 2, 7, 8, 9)
                .concatMap(x -> Observable.just(x, x*10)
                        .doOnSubscribe(() -> log("Subscribe for " + x))
                        .delay(y -> Observable.just(0).delay(randomLong(100), TimeUnit.MILLISECONDS)))
                .subscribe(Helpers::log);

        sleep(2000);
    }
}
