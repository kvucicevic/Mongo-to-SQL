package database;

import observer.ISubscriber;

public interface Query extends ISubscriber {

    Object runQuery();
}
