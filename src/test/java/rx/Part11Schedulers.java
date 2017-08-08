package rx;

import org.junit.Test;
import rx.schedulers.Schedulers;

import static rx.Helpers.log;
import static rx.Helpers.sleep;

public class Part11Schedulers {

    @Test
    public void ioScheduler() throws Exception {
        Observable.range(0, 20)
                .flatMap(x -> Observable.just(x).subscribeOn(Schedulers.io()).map(el -> {
                    log("Before work");
                    sleep(400); // emulate long execution
                    return el;
                }))
                .subscribe();

        sleep(500);
    }

    @Test
    public void computationScheduler() throws Exception {
        Observable.range(0, 20)
                .flatMap(x -> Observable.just(x).subscribeOn(Schedulers.computation()).map(el -> {
                    log("Before work");
                    sleep(400); // emulate long execution
                    return el;
                }))
                .subscribe();

        sleep(2000);
    }

    @Test
    public void immediateScheduler() throws Exception {
        Schedulers.immediate()
                .createWorker()
                .schedule(() -> log("inside action"));
    }

    @Test
    public void recursiveCallsWithImmediateScheduler() throws Exception {
        Scheduler.Worker worker = Schedulers.immediate().createWorker();

        worker.schedule(() -> {
            log("Begin of top-level action");
            worker.schedule(() -> {
                log("Begin of middle-level action");
                worker.schedule(() -> {
                    log("Inner action");
                });
                log("End of middle-level action");
            });
            log("End of top-level action");
        });

    }

    @Test
    public void recursiveWithTrampoline() throws Exception {
        Scheduler.Worker worker = Schedulers.trampoline().createWorker();
        // stack doesn't grow
        worker.schedule(() -> {
            log("Begin of first action");
            worker.schedule(() -> {
                log("Begin of second action");
                worker.schedule(() -> {
                    log("Third action");
                });
                log("End of second action");
            });
            log("End of first action");
        });
    }
}
