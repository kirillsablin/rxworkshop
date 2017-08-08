package rx;

import org.junit.Test;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.Random;

import static rx.Helpers.log;
import static rx.Helpers.sleep;

public class Part3ColdAndHotObservables {
    @Test
    public void subscribeSeveralTimeToColdObservable() throws Exception {
        Observable<String> observable = Observable.create(subscriber -> {
            log("Observation started!");
            subscriber.onNext("1");
            subscriber.onNext("2");
            subscriber.onCompleted();
        });
        observable.subscribe(System.out::println);
        observable.subscribe(System.out::println);
    }

    @Test
    public void connectableObservable() throws Exception {
        Observable<String> observable = Observable.create(subscriber -> {
            log("Observation started!");
            subscriber.onNext("1");
            subscriber.onCompleted();
        });

        ConnectableObservable<String> connectableObservable = observable.publish();

        connectableObservable.subscribe(System.out::println);
        connectableObservable.subscribe(System.out::println);

        log("after subscriptions");

        connectableObservable.connect();
    }

    @Test
    public void connectableObservableWithRefCount() throws Exception {
        Observable<String> observable = Observable.create(subscriber -> {
            log("Observation started!");
            int i = 0;
            while (!subscriber.isUnsubscribed()) {
                sleep(100);
                i ++;
                log("Next generated " + i);
                subscriber.onNext("Next " + i);
            }
            subscriber.onCompleted();
        });

        Observable<String> automatedConnectableObservable = observable.subscribeOn(Schedulers.io()).share();

        Subscription sub1 = automatedConnectableObservable.subscribe(System.out::println);
        Subscription sub2 = automatedConnectableObservable.subscribe(System.out::println);

        Thread.sleep(300);
        sub1.unsubscribe();
        Thread.sleep(300);
        sub2.unsubscribe();
        log("After unsubscribes");
        Thread.sleep(300);
    }

    @Test
    public void publishSubject() throws Exception {
        Subject<String, String> subject = PublishSubject.create();

        subject.onNext("1");

        Subscription sub1 = subject.subscribe(System.out::println); // doesn't block!

        subject.onNext("2");

        Subscription sub2 = subject.subscribe(System.out::println);

        subject.onNext("3");

        sub1.unsubscribe();
        sub2.unsubscribe();

        subject.subscribe(System.out::println);
        subject.onNext("4");
    }

    @Test
    public void serialize() throws Exception {
        Subject<String, String> subject = PublishSubject.create();

        subject.serialize().subscribe(next -> {
            log("Begin: " + next);
            sleep(new Random().nextInt(100));
            log("End: " + next);
        });

        Runnable runnable = () -> {
            for(int i = 0; i < 20; i++) {
                subject.onNext(Thread.currentThread().getName() + " " + i);
                sleep(60);
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();

        Thread.sleep(1000);
    }
}
