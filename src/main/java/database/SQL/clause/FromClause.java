package database.SQL.clause;

import database.SQL.SQLQuery;
import database.SQL.Table;
import database.SQL.condition.JoinCondition;

import java.util.LinkedList;
import java.util.List;

public class FromClause extends SQLClause {
    private Table table;
    private boolean hasJoins;
    private List<JoinCondition> joins;

    public FromClause(SQLQuery query) {
        super(query);
        this.hasJoins = false;
        this.joins = null;
    }

    public void addJoin(JoinCondition joinCondition) {
        if(!hasJoins) {
            joins = new LinkedList<>();
            hasJoins = true;
        }
        joins.add(joinCondition);
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        String result = "FROM " + table.getTableName();   /// null pointer
        if(hasJoins) {
            for (JoinCondition joinCondition : joins) {
                result = result.concat(joinCondition.toString());
                //result += joinCondition.toString();
            }
        }
        return result;
    }

    public Table getTable() {
        return table;
    }

    public boolean isHasJoins() {
        return hasJoins;
    }

    public List<JoinCondition> getJoins() {
        return joins;
    }

    public void setHasJoins(boolean hasJoins) {
        this.hasJoins = hasJoins;
    }
}
