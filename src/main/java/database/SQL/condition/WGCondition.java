package database.SQL.condition;

import database.SQL.Column;

public abstract class WGCondition {
    private Column conditionColumn;

    public WGCondition(Column conditionColumn) {
        this.conditionColumn = conditionColumn;
    }

    public Column getConditionColumn() {
        return conditionColumn;
    }
}
