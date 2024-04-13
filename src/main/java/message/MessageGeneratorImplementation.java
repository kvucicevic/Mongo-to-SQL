package message;

import observer.ISubscriber;

import java.util.LinkedList;
import java.util.List;

public class MessageGeneratorImplementation implements MessageGenerator {

    private final List<ISubscriber> subscribers;

    public MessageGeneratorImplementation() {
        this.subscribers = new LinkedList<>();
    }

    @Override
    public void addSub(ISubscriber sub) {
        this.subscribers.add(sub);
    }

    @Override
    public void removeSub(ISubscriber sub) {
        this.subscribers.remove(sub);
    }

    @Override
    public void notify(Object notification) {
        for(ISubscriber sub : subscribers) {
            sub.update(notification);
        }
    }

    @Override
    public void getMessage(String text) {
        notify(new Message(text));
    }

}
