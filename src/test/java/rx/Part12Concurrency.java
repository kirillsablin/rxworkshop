package rx;

import org.junit.Test;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;
import static rx.Helpers.sleep;

public class Part12Concurrency {

    @Test
    public void subscribeOn() throws Exception {
        Observable.fromCallable(() -> {
            log("OnSubscribe");
            return "value";
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Test
    public void severalSubscribeOn() throws Exception {
        Observable.fromCallable(() -> {
            log("OnSubscribe");
            return "value";
        })
                .subscribeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe();

        sleep(100);
    }

    @Test
    public void observeOn() throws Exception {
        Observable.defer(() -> {
            log("callable");
            return Observable.just("1", "2", "3");
        })
                .doOnNext(val -> log("value before observerOn: " + val))
                .observeOn(Schedulers.io())
                .doOnNext(val -> log("value after observerOn: " + val))
                .subscribe(val -> log("Value: " + val));
        log("After subscribe");
    }

    @Test
    public void observeOnWithError() throws Exception {
        Observable
                // emits 3 values then error
                .interval(300, TimeUnit.MILLISECONDS)
                .map(l -> (int) l.longValue())
                .take(3)
                .concatWith(Observable.error(new RuntimeException("Some error")))

                .observeOn(Schedulers.io())
                .doOnNext(val -> sleep(300)) // just for side effect
                .subscribe(
                        Helpers::log,
                        err -> Helpers.log(err.getMessage())
                );

        sleep(1000);


    }
}
