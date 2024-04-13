package database.SQL.clause;

import database.SQL.Column;
import database.SQL.OrderType;
import database.SQL.SQLQuery;

import java.util.HashMap;
import java.util.Map;

public class OrderClause extends SQLClause {

    private Map<Column,OrderType> orderColumns;
    public OrderClause(SQLQuery query) {
        super(query);
        this.orderColumns = new HashMap<>();
    }

    public void addOrder(Column column, String orderType) {
        orderColumns.put(column, OrderType.getOrderType(orderType));
    }

    public Map<Column, OrderType> getOrderColumns() {
        return orderColumns;
    }
}
