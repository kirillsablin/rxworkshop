package rx;

import org.junit.Test;
import rx.schedulers.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;
import static rx.Helpers.randomLong;
import static rx.Helpers.sleep;

public class Part19Debugging {

    @Test
    public void doOnSubscribe() throws Exception {
        Observable<String> obs = Observable.just("Value").doOnSubscribe(() -> log("Subscribed"));

        obs.subscribe();
        obs.subscribe();
    }

    @Test
    public void doOnComplete() throws Exception {
        Observable<String> obs = Observable.just("Value").observeOn(Schedulers.io()).doOnCompleted(() -> log("Completed"));

        obs.subscribe();
        obs.subscribe();
    }

    @Test
    public void measureTimeOfExternalCall() throws Exception {
        Observable<String> measured =
                Observable.fromCallable(Timer::new)
                    .flatMap(timer ->
                        externalCall().doOnCompleted(() -> log("Total time: " + timer.timeFromStart()))
                    );

        measured.subscribe();
        measured.subscribe();
        measured.subscribe();

        sleep(300);
    }

    private Observable<String> externalCall() {
        return Observable.timer(randomLong(100), TimeUnit.MILLISECONDS).map(l -> "Value");
    }

    private static class Timer {
        private Instant started = Instant.now();

        Duration timeFromStart() {
            return Duration.between(Instant.now(), started);
        }
    }
}
