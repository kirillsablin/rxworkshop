package rx;

import org.junit.Test;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;

public class Part18Hooks {
    @Test
    public void onObservableCreate() throws Exception {
        RxJavaHooks.setOnObservableCreate(ons -> {
            log("Created observable: " + ons);
            return ons;
        });

        Observable.just(1).subscribe();
        Observable.just(1).subscribe();
    }

    @Test
    public void inComputationScheduler() throws Exception {
        TestScheduler testScheduler = Schedulers.test();
        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);

        Observable.just("event").delay(10, TimeUnit.MINUTES).subscribe(Helpers::log);

        testScheduler.advanceTimeBy(12, TimeUnit.MINUTES);
    }
}
