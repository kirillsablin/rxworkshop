package rx;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static rx.Helpers.randomLong;

public class Part14Buffering {
    @Test
    public void simpleBuffering() throws Exception {
        Observable.range(1, 20)
                .buffer(3)
                .map(Object::toString)
                .subscribe(Helpers::log);
    }

    @Test
    public void simpleBuffering2() throws Exception {
        Observable.range(1, 20)
                .buffer(3, 1)
                .map(Object::toString)
                .subscribe(Helpers::log);
    }

    @Test
    public void bufferingByObservable() throws Exception {
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .buffer(Observable.interval(30, TimeUnit.MILLISECONDS), i -> Observable.just(10).delay(15, TimeUnit.MILLISECONDS))
                .map(Object::toString)
                .take(10)
                .toBlocking()
                .subscribe(Helpers::log);

    }

    @Test
    public void windowWithClosing() throws Exception {
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .window(() -> Observable.timer(randomLong(30), TimeUnit.MILLISECONDS))
                .concatMap(Observable::count)
                .take(10)
                .toBlocking()
                .subscribe(Helpers::log);

    }

    @Test
    public void windowUnicastSubObservables() throws Exception {
        List<Observable<Integer>> result
                = Observable.range(1, 10).window(3).toList().toBlocking().single();

        result.get(0).subscribe(Helpers::log);
        result.get(3).subscribe(Helpers::log);
        result.get(1).subscribe(Helpers::log);

    }
}
