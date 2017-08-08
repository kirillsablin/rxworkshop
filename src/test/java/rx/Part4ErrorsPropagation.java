package rx;

import org.junit.Test;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static rx.Helpers.log;

public class Part4ErrorsPropagation {
    @Test
    public void exceptionInOnSubscribe() throws Exception {
        Observable.create(subscriber -> {
            throw new RuntimeException("Error");
        }).subscribe(
                obj -> {},
                err -> log(err.toString()));
    }

    @Test
    public void exceptionInOperator() throws Exception {
        Observable.just(5).map(val -> { throw new RuntimeException("Error"); }).subscribe(
                obj -> {},
                err -> log(err.toString())
        );
    }

    @Test
    public void subjectErrorPropagation() throws Exception {
        Subject<String, String> subject = PublishSubject.create();

        subject.subscribe(System.out::println, err -> log("first subscriber " + err.toString()));

        subject.onNext("Before error");
        subject.onError(new RuntimeException("err"));
        subject.onNext("After error"); // will not be shown

        subject.subscribe(System.out::println, err -> log("second subscriber " + err.toString()));
        subject.onNext("After error2"); // will not be shown
    }
}
