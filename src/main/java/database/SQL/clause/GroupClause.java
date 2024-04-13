package database.SQL.clause;

import database.SQL.Column;
import database.SQL.LogicalOperator;
import database.SQL.SQLQuery;
import database.SQL.condition.WGCondition;

import java.util.LinkedList;
import java.util.List;

public class GroupClause extends SQLClause{
    private List<Column> groupColumns;

    public GroupClause(SQLQuery query) {
        super(query);
        this.groupColumns = new LinkedList<>();
    }

    public void addGroupColumn(Column column) {
        this.groupColumns.add(column);
    }

    public List<Column> getGroupColumns() {
        return groupColumns;
    }
}
