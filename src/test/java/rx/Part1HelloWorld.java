package rx;

import org.junit.Test;
import rx.schedulers.Schedulers;

import static rx.Helpers.sleep;

public class Part1HelloWorld {
    @Test
    public void helloWorld() throws Exception {
        Observable.just("Hello world")
                .subscribe(System.out::println);
    }

    @Test
    public void helloWorldFromCallable() throws Exception {
        Observable.<String>create(subscriber -> {
            subscriber.onNext("Hello world");
            subscriber.onCompleted();
        })
                .subscribe(System.out::println);

    }

    @Test
    public void helloWorldWithError() throws Exception {
        Observable.<String>create(subscriber -> {
            subscriber.onNext("Hello world");
            throw new RuntimeException("Exception");
        })
                .subscribe(System.out::println);
    }

    @Test
    public void fullSubscriberAsSetOfFunctions() throws Exception {
        Observable.<String>create(subscriber -> {
            subscriber.onNext("Hello world");
            subscriber.onCompleted();
        })
                .subscribe(
                        System.out::println,
                        err -> System.out.println("err " + err.getMessage()),
                        () -> System.out.println("Complete")
                );
    }

    @Test
    public void fullSubscriberAsAClass() throws Exception {
        Observable.<String>create(subscriber -> {
            subscriber.onNext("Hello world");
            subscriber.onCompleted();
        })
                .subscribe(new Subscriber<String>() {
                               @Override
                               public void onCompleted() {
                                   System.out.println("Complete");
                               }
                               @Override
                               public void onError(Throwable e) {
                                   System.out.println("err " + e.getMessage());
                               }
                               @Override
                               public void onNext(String s) {
                                   System.out.println(s);
                               }
                           }
                );
    }

    @Test
    public void slowHelloWorld() throws Exception {
        Observable.<String>create(subscriber -> {
            sleep(3000);
            subscriber.onNext("Hello world");
            subscriber.onCompleted();
        })
                .subscribe(System.out::println);

    }

    @Test
    public void shutdownCallback() throws Exception {
        Observable<String> observable = Observable.<String>create(subscriber -> {
            subscriber.add(new Subscription() {
                @Override
                public void unsubscribe() {
                    System.out.println("Shutdown!");
                }

                @Override
                public boolean isUnsubscribed() {
                    return false;
                }
            });
            subscriber.onNext("Hello world");

        }).subscribeOn(Schedulers.io());

        Subscription subscription = observable.subscribe(System.out::println);
        subscription.unsubscribe();
    }
}
