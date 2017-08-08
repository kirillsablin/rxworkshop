package rx;

import org.junit.Test;

public class Part9CustomOperators {
    @Test
    public void notSharedComposition() throws Exception {
        Observable.just(1, 2, 3)
                .map(i -> i * 113)
                .filter(i -> i > 100) // this and next operators
                .map(Integer::toHexString) // should be shared between different parts of code
                .map(s -> "Prefix: " + s)
                .subscribe(Helpers::log);
    }

    @Test
    public void sharedAsFunction() throws Exception {
        Observable<Integer> obs = Observable.just(1, 2, 3);

        sharedFunction(
                obs.map(i -> i * 113)
        )
                .map(s -> "Prefix: " + s)
                .subscribe(Helpers::log);
    }

    private Observable<String> sharedFunction(Observable<Integer> obs) {
        return obs
                .filter(i -> i > 100)
                .map(Integer::toHexString);
    }

    @Test
    public void sharedAsComposition() throws Exception {
        Observable.just(1, 2, 3)
                .map(i -> i * 113)
                .compose(composition())
                .map(s -> "Prefix: " + s)
                .subscribe(Helpers::log);
    }

    private Observable.Transformer<Integer, String> composition() {
        return integerObservable ->
                integerObservable.filter(i -> i > 100).map(Integer::toHexString);
    }

    @Test
    public void lift() throws Exception {
        Observable.Operator<String, Integer> operator = new Observable.Operator<String, Integer>() {
            @Override
            public Subscriber<? super Integer> call(Subscriber<? super String> child) {
                return new Subscriber<Integer>(child) { // passing child subscriber is crucial for subscription machinery
                    private boolean applyCurrent = false; // not thread safe, memory model is not honored

                    @Override
                    public void onCompleted() {
                        child.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        applyCurrent = !applyCurrent;
                        if (applyCurrent) {
                            child.onNext(integer.toString());
                        }
                        // backpressure implemented incorrectly
                    }
                };
            }
        };


        Observable.just(1, 2, 3, 4).lift(operator).subscribe(Helpers::log);
    }
}
