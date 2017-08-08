package rx;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static rx.Helpers.randomLong;

public class Part10BlockingObservable {
    @Test
    public void allEventsToBlockingList() throws Exception {
        List<Integer> result = Observable.just(1, 2, 3)
                .toList()
                .toBlocking()
                .single();

        System.out.println(result);
    }

    @Test
    public void firstBlockingResult() throws Exception {
        int result = Observable.just(1, 2, 3).toBlocking().first();

        System.out.println(result);
    }

    @Test
    public void blockingForEach() throws Exception {
        Observable.just(1, 2, 3).flatMap(i -> Observable.just(i*2, i*3).delay(randomLong(800), TimeUnit.MILLISECONDS))
                .toBlocking()
                .forEach(Helpers::log); // blocks until observable is completed
    }

    @Test
    public void errorAndBlocking() throws Exception {
        Observable.error(new RuntimeException("some error"))
                .toBlocking()
                .firstOrDefault("12");
    }
}
