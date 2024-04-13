package database.SQL.clause;

import database.SQL.LogicalOperator;
import database.SQL.SQLQuery;
import database.SQL.condition.WGCondition;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class WhereClause extends SQLClause {
    private List<WGCondition> conditionList;
    private List<LogicalOperator> logicalOperators;
    public WhereClause(SQLQuery query) {
        super(query);
        this.conditionList = new LinkedList<>();
        this.logicalOperators = new LinkedList<>();
    }

    public void addCondition(WGCondition condition) {
        this.conditionList.add(condition);
    }

    public void addLogicalOperator(String logicalOperator) {this.logicalOperators.add(LogicalOperator.getLogicalOperator(logicalOperator)); }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("WHERE ");
        ListIterator<LogicalOperator> logicalOperatorListIterator = logicalOperators.listIterator();
        for(WGCondition condition : conditionList) {
            result.append(condition.toString()).append(" ");
            if(logicalOperatorListIterator.hasNext())
                result.append(logicalOperatorListIterator.next().name()).append(" ");
        }
        return result.toString().trim();
    }

    public List<WGCondition> getConditionList() {
        return conditionList;
    }

    public List<LogicalOperator> getLogicalOperators() {
        return logicalOperators;
    }
}
