package database.SQL;

public enum OrderType {
    ASC,
    DESC;

    public static OrderType getOrderType(String orderType) {
        if(orderType == null || orderType.equalsIgnoreCase("asc"))
            return ASC;
        else if(orderType.equalsIgnoreCase("desc"))
            return DESC;
        else
            return null;
    }
}
