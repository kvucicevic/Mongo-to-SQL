package database.SQL.clause;

import database.SQL.SQLQuery;

public abstract class SQLClause {

    private SQLQuery query;

    public SQLClause(SQLQuery query) {
        this.query = query;
        this.query.addClause(this);
    }

    public SQLQuery getQuery() {
        return query;
    }
}
