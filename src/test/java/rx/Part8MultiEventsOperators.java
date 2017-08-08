package rx;

import org.junit.Test;

import static rx.Helpers.log;

public class Part8MultiEventsOperators {
    @Test
    public void scan() throws Exception {
        Observable.just(1, 2, 3, 4).scan("", (acc, i) -> acc + i).subscribe(Helpers::log);
    }

    @Test
    public void reduce() throws Exception {
        Observable.just(1, 2, 3, 4).reduce("", (acc, i) -> acc + i).subscribe(Helpers::log);

        // same result as
        Observable.just(1, 2, 3, 4).scan("", (acc, i) -> acc + i).takeLast(1).subscribe(Helpers::log);
    }

    @Test
    public void collect() throws Exception {
        Observable.just(1, 2, 3, 4)
                .collect(StringBuilder::new, StringBuilder::append)
                .map(StringBuilder::toString)
                .subscribe(Helpers::log);
    }

    @Test
    public void single() throws Exception {
        Observable.<String>just("1").single().subscribe(Helpers::log);
    }

    @Test
    public void distinct() throws Exception {
        Observable.just("1", "1", "2", "3").distinct().subscribe(Helpers::log);
    }

    @Test
    public void distinctUntilChanged() throws Exception {
        Observable.just("1", "1", "2", "2", "3", "2").distinctUntilChanged().subscribe(Helpers::log);
    }

    @Test
    public void all() throws Exception {
        Observable.just(1, 2, 3).all( i -> i > 0).subscribe(Helpers::log);
    }

    @Test
    public void groupBy() throws Exception {
        Observable.range(0, 30)
                .groupBy(i -> i % 4)
                .subscribe(byQuoters ->
                    byQuoters.subscribe(val -> log(byQuoters.getKey() + ": " +val))
                );
    }
}
