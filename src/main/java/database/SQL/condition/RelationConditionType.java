package database.SQL.condition;

public enum RelationConditionType {
    GT,
    GTE,
    LT,
    LTE,
    EQ;

    public static RelationConditionType getRelationConditionType(String relationConditionType) {
        if(relationConditionType.equalsIgnoreCase(">"))
            return GT;
        else if(relationConditionType.equalsIgnoreCase(">="))
            return GTE;
        else if(relationConditionType.equalsIgnoreCase("<"))
            return LT;
        else if(relationConditionType.equalsIgnoreCase("<="))
            return LTE;
        else if(relationConditionType.equalsIgnoreCase("="))
            return EQ;
        else
            return null;
    }

    @Override
    public String toString() {
        switch (this) {
            case EQ:
                return "=";
            case GT:
                return ">";
            case GTE:
                return ">=";
            case LTE:
                return "<=";
            case LT:
                return "<";
            default:
                return null;
        }
    }
}
