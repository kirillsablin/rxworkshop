package rx;

import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;

public class Part17TestsSupport {

    @Test
    public void basicAssertions() throws Exception {
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();

        Observable.just(1, 2, 3, 4)
                .subscribe(subscriber);

        subscriber.assertCompleted();
        subscriber.assertReceivedOnNext(Arrays.asList(1, 2, 3, 4));
        subscriber.assertValues(1, 2, 3, 4);
        subscriber.assertUnsubscribed();
    }

    @Test
    public void spySubscriber() throws Exception {
        TestSubscriber<String> subscriber = TestSubscriber.create(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                log("Error "+ e.getMessage());
            }

            @Override
            public void onNext(String s) {

            }
        });

        Observable.<String>error(new RuntimeException("Some error"))
                .subscribe(subscriber);

        subscriber.assertTerminalEvent();
        subscriber.assertNoValues();
        subscriber.assertError(RuntimeException.class);
    }

    @Test
    public void intervalWithExplicitScheduler() throws Exception {
        TestScheduler scheduler = Schedulers.test();
        Observable<Long> obs = Observable.interval(20, 200, TimeUnit.SECONDS, scheduler).take(5);
        TestSubscriber<Long> subscriber = new TestSubscriber<>();
        obs.subscribe(subscriber);

        subscriber.assertNoValues();
        subscriber.assertNoErrors();

        scheduler.advanceTimeBy(30, TimeUnit.SECONDS);
        subscriber.assertValue(0L);

        scheduler.advanceTimeBy(150, TimeUnit.SECONDS);
        subscriber.assertValue(0L);

        scheduler.advanceTimeBy(50, TimeUnit.SECONDS);
        subscriber.assertValues(0L, 1L);

        scheduler.advanceTimeBy(1000, TimeUnit.SECONDS);
        subscriber.assertValues(0L, 1L, 2L, 3L, 4L);
        subscriber.assertCompleted();


    }
}
