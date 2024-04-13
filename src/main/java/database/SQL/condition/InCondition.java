package database.SQL.condition;

import database.SQL.Column;

import java.util.LinkedList;
import java.util.List;

public class InCondition extends WGCondition {
    private List<Object> values;
    private boolean isSubQuery;
    public InCondition(Column conditionColumn, boolean isSubQuery) {
        super(conditionColumn);
        this.isSubQuery = isSubQuery;
        this.values = new LinkedList<>();
    }

    public boolean isSubQuery() {
        return isSubQuery;
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public List<Object> getValues() {
        return values;
    }
}
