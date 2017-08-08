package rx;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Part13Sampling {
    @Test
    public void sample() throws Exception {
        Observable.interval(10, TimeUnit.MILLISECONDS).sample(100, TimeUnit.MILLISECONDS)
                .take(10)
                .subscribe(Helpers::log);

        Thread.sleep(1000);
    }

    @Test
    public void debounce() throws Exception {
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .flatMap(i -> Observable.just("First " + i, "Second " + i, "Third " + i))
                .debounce(5, TimeUnit.MILLISECONDS)
                .take(10)
                .subscribe(Helpers::log);

        Thread.sleep(1000);
    }
}
