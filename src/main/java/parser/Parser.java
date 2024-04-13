package parser;

import database.Query;
import observer.IPublisher;

public interface Parser extends IPublisher {

    Query parse(String sQuery, boolean isSubQuery);

}
