package message;

import observer.IPublisher;

public interface MessageGenerator extends IPublisher {
    void getMessage(String text);
}
