package database.SQL;

import database.Query;
import database.SQL.clause.SQLClause;

import java.util.LinkedList;
import java.util.List;

public class SQLQuery implements Query{

    private List<SQLClause> clauses;

    public SQLQuery() {
        this.clauses = new LinkedList<>();
    }

    public void addClause(SQLClause clause) {
        this.clauses.add(clause);
    }

    public List<SQLClause> getClauses() {
        return clauses;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(SQLClause clause : clauses) {
            result.append(clause.toString()).append(" ");
        }
        return result.toString().trim();
    }

    @Override
    public Object runQuery() {
        return null;
    }

    @Override
    public void update(Object notification) {

    }
}
