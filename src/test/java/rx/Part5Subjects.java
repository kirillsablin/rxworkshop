package rx;

import org.junit.Test;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;
import rx.subjects.UnicastSubject;

public class Part5Subjects {

    @Test
    public void asyncSubject() throws Exception {
        Subject<String, String> subject = AsyncSubject.create();

        subject.subscribe(Helpers::log);

        subject.onNext("Invisible");
        subject.onNext("Before complete");
        subject.onCompleted();

        subject.subscribe(Helpers::log); // last value will be cached
    }

    @Test
    public void behaviorSubject() throws Exception {
        Subject<String, String> subject = BehaviorSubject.create();

        subject.subscribe(val -> Helpers.log("Subscriber1: " + val));
        subject.onNext("val1");
        subject.subscribe(val -> Helpers.log("Subscriber2: " + val));
        subject.onNext("val2");
        subject.subscribe(val -> Helpers.log("Subscriber3: " + val));
    }

    @Test
    public void replaySubject() throws Exception {
        Subject<String, String> subject = ReplaySubject.create();
        subject.onNext("val1");
        subject.onNext("val2");
        subject.subscribe(val -> Helpers.log("Subscriber1: " + val));
        subject.onNext("val3");
        subject.subscribe(val -> Helpers.log("Subscriber2: " + val));
        subject.onNext("val4");
    }

    @Test
    public void replaySubjectWithLimitedCapacity() throws Exception {
        Subject<String, String> subject = ReplaySubject.createWithSize(2);

        subject.onNext("val1");
        subject.onNext("val2");
        subject.onNext("val3");

        subject.subscribe(Helpers::log);
    }

    @Test
    public void unicastSubject() throws Exception {
        Subject<String, String> subject = UnicastSubject.create();

        subject.onNext("val1");
        subject.onNext("val2");

        subject.subscribe(val -> Helpers.log("Subscriber1: " + val));

        subject.onNext("val3");
    }
}
