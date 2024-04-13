package database.SQL.condition;

import database.SQL.Column;

public class RelationCondition extends WGCondition{

    private RelationConditionType relationConditionType;
    private Object referenceValue;
    public RelationCondition(Column conditionColumn, String relationConditionType, Object referenceValue) {
        super(conditionColumn);
        this.referenceValue = referenceValue;
        this.relationConditionType = RelationConditionType.getRelationConditionType(relationConditionType);
    }

    @Override
    public String toString() {
        return getConditionColumn().getColumnName() + " " + relationConditionType.toString() + " " + referenceValue.toString();
    }

    public RelationConditionType getRelationConditionType() {
        return relationConditionType;
    }

    public Object getReferenceValue() {
        return referenceValue;
    }
}
