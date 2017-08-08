package rx;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static rx.Helpers.log;
import static rx.Helpers.randomLong;

public class Part16ErrorHandling {
    @Test
    public void onErrorReturn() throws Exception {
        Observable.<Integer>create(subscriber -> {
            subscriber.onNext(10);
            subscriber.onNext(11);
            subscriber.onError(new RuntimeException("error"));
        })
                .onErrorReturn(err -> 13)
                .subscribe(Helpers::log,
                        err -> log(err.getMessage()));

    }

    @Test
    public void onErrorResume() throws Exception {
        Observable<String> obs = unstableObservable();

        addErrorResume(obs)
                .subscribe(Helpers::log);

    }

    private Observable<String> addErrorResume(Observable<String> source) {
        return source
                .onErrorResumeNext(err -> {
                    log("Resume after error: " + err.toString());
                    return addErrorResume(source);
                });
    }

    @Test
    public void onActionError() throws Exception {
        Observable.range(1, 10)
                .flatMap(id -> doQuery(id).onErrorReturn(Throwable::getMessage))
                .subscribe(Helpers::log);
    }

    private Observable<String> doQuery(int id) {
        if (randomLong(100) > 70) {
            return Observable.error(new RuntimeException("Query failed"));
        } else {
            return Observable.just("Value for " + id);
        }
    }

    @Test
    public void retry() throws Exception {
        Observable<String> obs = unstableObservable();

        obs.retry(10).subscribe();
    }

    private Observable<String> unstableObservable() {
        return Observable.create(subscriber -> {
            log("Subscribed!");
            if (randomLong(100) > 90) {
                subscriber.onNext("Value");
                subscriber.onCompleted();
            } else {
                subscriber.onError(new RuntimeException("Error"));
            }
        });
    }

    @Test
    public void retryWhen() throws Exception {
        Observable.error(new RuntimeException("Err"))
                .doOnSubscribe(() -> log("Subscribed!"))
                .retryWhen(errors ->
                        errors.zipWith(Observable.range(1, 5), (err, i) -> i)
                            .flatMap(i -> Observable.timer(i, TimeUnit.SECONDS))
                )
                .toBlocking()
                .subscribe();

    }
}
