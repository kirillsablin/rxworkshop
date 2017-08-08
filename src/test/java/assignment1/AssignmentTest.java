package assignment1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AssignmentTest {
    private TestScheduler testScheduler = Schedulers.test();
    private TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
    private ExternalSource externalSource = new TestExternalSource(testScheduler);

    @Test
    public void calculateByZeroKeys() throws Exception {
        SumCalculator sumCalculator = new SumCalculator();

        sumCalculator.calculate(ImmutableList.of()).subscribe(testSubscriber);
        testScheduler.triggerActions();

        testSubscriber.assertValue(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void calculateManyKeys() throws Exception {
        SumCalculator sumCalculator = new SumCalculator();

        sumCalculator.calculate(ImmutableList.of(1, 2, 3, 4, 5)).subscribe(testSubscriber);

        testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);
        testSubscriber.assertNoValues();

        testScheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS);
        testSubscriber.assertValue(159);
        testSubscriber.assertCompleted();
    }

    private static class TestExternalSource implements ExternalSource {
        private final Scheduler scheduler;

        private final Map<Integer, Integer> answers = ImmutableMap.of(1, 10, 2, 21, 3, 33, 4, 41, 5, 54);

        public TestExternalSource(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public Observable<Integer> loadData(int key) {
            if (!answers.containsKey(key)) {
                return Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).cast(Integer.class).skip(1);
            } else {
                return Observable.timer(answers.get(key) * 10, TimeUnit.MILLISECONDS, scheduler).map(l -> answers.get(key));
            }
        }

    }
}
