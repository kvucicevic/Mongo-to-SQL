package observer;

public interface IPublisher {

    void addSub(ISubscriber sub);
    void removeSub(ISubscriber sub);
    void notify(Object notification);
}
