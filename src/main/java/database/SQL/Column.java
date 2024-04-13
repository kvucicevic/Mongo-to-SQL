package database.SQL;

import java.util.Objects;

public class Column {
    private String columnName;
    private boolean isAggregate;
    private AggregateFunction aggregateFunction;

    public Column(String columnName) {
        this.columnName = columnName;
        this.isAggregate = false;
        this.aggregateFunction = AggregateFunction.NONE;
    }

    public Column(String columnName, String aggregateFunction) {
        this.columnName = columnName;
        this.isAggregate = true;
        this.aggregateFunction = AggregateFunction.getAggregateFunction(aggregateFunction);
    }

    public boolean isAggregate() {
        return isAggregate;
    }

    public String getColumnName() {
        return columnName;
    }

    public AggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return isAggregate == column.isAggregate && columnName.equals(column.columnName) && aggregateFunction == column.aggregateFunction;
    }

}
