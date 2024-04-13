package database.SQL.condition;

import database.SQL.Column;

public class LikeCondition extends WGCondition{

    private String referenceValue;

    public LikeCondition(Column conditionColumn, String referenceValue) {
        super(conditionColumn);
        this.referenceValue = referenceValue;
    }

    public String getReferenceValue() {
        return referenceValue;
    }
}
