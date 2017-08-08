package rx;

import org.junit.Test;

import static rx.Helpers.log;

public class Part2SingleAndCompletable {

    @Test
    public void single() throws Exception {
        Single.fromCallable(() -> "value")
                .subscribe(System.out::println);
    }

    @Test
    public void completable() throws Exception {
        Completable.fromAction(() -> log("Action")).subscribe(() -> log("complete"));

    }
}
