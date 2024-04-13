package database.SQL;

public enum AggregateFunction {
    NONE,
    SUM,
    AVG,
    MAX,
    MIN,
    COUNT;

    public static AggregateFunction getAggregateFunction(String name) {
        if(name == null)
            return NONE;
        else if(name.equalsIgnoreCase("sum"))
            return SUM;
        else if(name.equalsIgnoreCase("avg"))
            return AVG;
        else if(name.equalsIgnoreCase("max"))
            return MAX;
        else if(name.equalsIgnoreCase("min"))
            return MIN;
        else if(name.equalsIgnoreCase("count"))
            return COUNT;
        else
            return null;
    }
}
