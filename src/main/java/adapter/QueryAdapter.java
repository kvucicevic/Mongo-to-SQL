package adapter;

import observer.IPublisher;
import observer.ISubscriber;

public interface QueryAdapter extends ISubscriber, IPublisher {

    void queryConverter();
}
