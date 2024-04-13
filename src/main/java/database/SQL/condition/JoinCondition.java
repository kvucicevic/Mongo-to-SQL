package database.SQL.condition;

import database.SQL.Column;
import database.SQL.Table;

public class JoinCondition {
    private JoinConditionType joinConditionType;
    private Table joinTable;
    private Column conditionColumn;
    private Column conditionColumnOn;

    public JoinCondition(Table joinTable, Column conditionColumn) {
        this.joinConditionType = JoinConditionType.USING;
        this.joinTable = joinTable;
        this.conditionColumn = conditionColumn;
        this.conditionColumnOn = null;
    }

    public JoinCondition(Table joinTable, Column conditionColumn, Column conditionColumnOn) {
        this.joinConditionType = JoinConditionType.ON;
        this.joinTable = joinTable;
        this.conditionColumn = conditionColumn;
        this.conditionColumnOn = conditionColumnOn;
    }

    @Override
    public String toString() {
        if(joinConditionType == JoinConditionType.ON) {
            return " JOIN " + joinTable.getTableName() + " ON (" + conditionColumn.getColumnName() + "=" + conditionColumnOn.getColumnName() + ")";
        } else {
            return " JOIN " + joinTable.getTableName() + " USING (" + conditionColumn.getColumnName() + ")";
        }
    }

    public Column getConditionColumn() {
        return conditionColumn;
    }

    public Column getConditionColumnOn() {
        return conditionColumnOn;
    }

    public JoinConditionType getJoinConditionType() {
        return joinConditionType;
    }

    public Table getJoinTable() {
        return joinTable;
    }

}
