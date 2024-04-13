package parser.sql;

public enum SQLKeyword {
    SELECT,
    FROM,
    JOIN,
    ON,
    USING,
    WHERE,
    GROUP,
    BY,
    BETWEEN,
    AND,
    IN,
    NOT,
    LIKE,
    OR,
    HAVING,
    ORDER,
    ASC,
    DESC;

    public static boolean checkKeyword(String token) {
        for(SQLKeyword keyword : SQLKeyword.values()) {
            if(token.equalsIgnoreCase(keyword.name()))
                return true;
        }
        return false;
    }
}
