package database.SQL;

public enum LogicalOperator {
    AND,
    OR;

    public static LogicalOperator getLogicalOperator(String logicalOperator) {
        if(logicalOperator.equalsIgnoreCase("and"))
            return AND;
        else if(logicalOperator.equalsIgnoreCase("or"))
            return OR;
        else
            return null;
    }
}
