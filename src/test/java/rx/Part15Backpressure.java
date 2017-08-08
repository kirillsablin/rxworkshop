package rx;

import org.junit.Test;
import rx.observables.SyncOnSubscribe;
import rx.schedulers.Schedulers;

import static rx.Helpers.log;
import static rx.Helpers.sleep;

public class Part15Backpressure {
    @Test
    public void exampleOfBackpressure() throws Exception {
        Observable.range(1, 1000)
                .doOnNext(Helpers::log)
                .doOnRequest(l -> log("Requested: " + l))
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    sleep(5);
                });

        sleep(5000);
    }

    @Test
    public void backpressureAbsence() throws Exception {
        Observable.<Integer>create(subscriber -> {
            for (int i = 0; i < 1000; i++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(i);
                }
            }
            subscriber.onCompleted();
        })
                .doOnNext(Helpers::log)
                .doOnRequest(l -> log("Requested: " + l))
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    sleep(1);
                });
        Thread.sleep(300);
    }

    @Test
    public void manualBackpressure() throws Exception {
        Observable.<Integer>create(subscriber -> subscriber.setProducer(
                new Producer() {
                    int curr = 0; // potentially not thread safe

                    @Override
                    public void request(long n) {
                        for (int i = 0; i < n; i++) {
                            if (curr < 100) {
                                subscriber.onNext(curr++);
                            } else {
                                subscriber.onCompleted();
                            }
                        }
                    }
                }
        ))
                .doOnRequest(l -> log("Requested " + l))
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onStart() {
                        request(1);
                    }

                    @Override
                    public void onCompleted() {
                        log("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer o) {
                        log("value " + o.toString());
                        request(1);
                    }
                });
    }

    @Test
    public void semiManualBackpressure() throws Exception {
        Observable.create(new SyncOnSubscribe<Integer, String>() {
            @Override
            protected Integer generateState() {
                return 0;
            }

            @Override
            protected Integer next(Integer state, Observer<? super String> observer) {
                if (state > 1000) {
                    observer.onCompleted();
                    return 1001;
                } else {
                    state++;
                    observer.onNext("State: " + state);
                    return state;
                }
            }
        })
                .doOnNext(Helpers::log)
                .doOnRequest(l -> log("Requested: " + l))
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    sleep(1);
                });

        sleep(1000);
    }

    @Test
    public void onBackpressureBuffer() throws Exception {
        Observable.<Integer>create(subscriber -> {
            for (int i = 0; i < 1000; i++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(i);
                }
            }
            subscriber.onCompleted();
        })
                .onBackpressureBuffer()
                .doOnNext(Helpers::log)
                .doOnRequest(l -> log("Requested: " + l))
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    sleep(1);
                });
        Thread.sleep(300);

    }
}
