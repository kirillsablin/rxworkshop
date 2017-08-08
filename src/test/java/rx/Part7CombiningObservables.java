package rx;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;
import static rx.Helpers.randomLong;
import static rx.Helpers.sleep;

public class Part7CombiningObservables {
    @Test
    public void merge() throws Exception {
        Observable.merge(
                Observable.just(1)
                        .doOnSubscribe(() -> log("Inner Subscribe"))
                        .delay(randomLong(100), TimeUnit.MILLISECONDS),
                Observable.just(2)
                        .doOnSubscribe(() -> log("Inner Subscribe"))
                        .delay(randomLong(100), TimeUnit.MILLISECONDS)
        )
                .doOnSubscribe(() -> log("Subscribe"))
                .subscribe(Helpers::log);

        sleep(100);
    }

    @Test
    public void concat() throws Exception {
        Observable.concat(
                Observable.just(1)
                        .doOnSubscribe(() -> log("Inner Subscribe"))
                        .delay(randomLong(100), TimeUnit.MILLISECONDS),
                Observable.just(2)
                        .doOnSubscribe(() -> log("Inner Subscribe"))
                        .delay(randomLong(100), TimeUnit.MILLISECONDS)
        )
                .doOnSubscribe(() -> log("Subscribe"))
                .subscribe(Helpers::log);

        sleep(200);
    }

    @Test
    public void concatAsFallback() throws Exception {
        Observable<String> fromCache = Observable.empty();
        Observable<String> fromDb = Observable.just("data");

        Observable.concat(fromCache, fromDb).first().subscribe(Helpers::log);
    }

    @Test
    public void zipAndZipWith() throws Exception {
        log("Zip");
        Observable.zip(
                Observable.just(1, 2),
                Observable.just(3, 4, 5),
                (a, b) -> a + " - " + b
        ).subscribe(Helpers::log);

        log("ZipWith");
        Observable.just(1, 2, 3)
                .zipWith(Observable.just(3, 4), (a, b) -> a + " - " + b)
                .subscribe(Helpers::log);
    }

    @Test
    public void combineLatest() throws Exception {
        Observable.combineLatest(
                Observable.just(1, 2, 3, 4).delay(i -> Observable.just(0).delay(100 * i, TimeUnit.MILLISECONDS)),
                Observable.just(10, 20, 30, 40).delay(i -> Observable.just(0).delay(30 * i, TimeUnit.MILLISECONDS)),
                (x, y) -> x + y
                )
                .subscribe(Helpers::log);

        sleep(1300);
    }

    @Test
    public void withLatestFrom() throws Exception {
        // 1 appears before any event from second observable. That's why we need startWith(0) to have element to combine
        Observable.just(1, 20, 30, 40).delay(i -> Observable.just(0).delay(30 * i, TimeUnit.MILLISECONDS))
                .withLatestFrom(
                        Observable.just(1, 2, 3, 4).delay(i -> Observable.just(0).delay(100 * i, TimeUnit.MILLISECONDS))
                                .startWith(0),
                        (x, y) -> x + y
                )
                .subscribe(Helpers::log);

        sleep(1300);
    }

    @Test
    public void amb() throws Exception {
        Observable.amb(
                Observable.just(1, 2, 3).delay(randomLong(80), TimeUnit.MILLISECONDS),
                Observable.just(4, 5, 6).delay(randomLong(80), TimeUnit.MILLISECONDS)
        )
                .subscribe(Helpers::log);

        sleep(100);

    }
}
