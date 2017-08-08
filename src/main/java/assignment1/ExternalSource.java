package assignment1;

import rx.Observable;

public interface ExternalSource {
    Observable<Integer> loadData(int key);
}
