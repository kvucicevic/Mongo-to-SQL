package parser.sql;

import database.SQL.*;
import database.SQL.clause.*;
import database.SQL.condition.*;
import gui.MainFrame;
import gui.error.ErrorDialog;
import observer.ISubscriber;
import parser.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SQLParser implements Parser {

    private List<ISubscriber> subs;
    private boolean foundError;
    public SQLParser() {
        this.subs = new LinkedList<>();
    }

    /** parser je implementiran pre smanjenja funkcionalnosti
     ** parser uspesno parsira i podupite koji nisu u WHERE itd.
     **/
    @Override
    public SQLQuery parse(String sQuery, boolean isSubQuery) {
        foundError = false;
        String sQueryLowerCase = sQuery.toLowerCase();
        List<String> tokens = List.of(sQueryLowerCase.split(" +"));
        ListIterator<String> listIterator = tokens.listIterator();
        SQLQuery sqlQuery = new SQLQuery();
        boolean hasSelect = false;
        boolean hasFrom = false;
        while (listIterator.hasNext()){
            String token = listIterator.next();
            switch (token) {
                case "select":
                    generateSelectClause(listIterator, sqlQuery);
                    if(foundError) return null;
                    hasSelect = true;
                    break;
                case "from":
                    if (!hasSelect) {
                        error("SELECT clause not found");
                    }
                    generateFromClause(listIterator, sqlQuery);
                    if(foundError) return null;
                    hasFrom = true;
                    break;
                case "where":
                    if (!hasFrom) {
                        error("FROM clause not found");
                    }
                    generateWhereClause(listIterator, sqlQuery);
                    if(foundError) return null;
                    break;
                case "group":
                    if (!hasFrom) {
                        error("FROM clause not found");
                    }
                    generateGroupClause(listIterator, sqlQuery);
                    if(foundError) return null;
                    break;
                case "order":
                    if (!hasFrom) {
                        error("FROM clause not found");
                    }
                    generateOrderClause(listIterator, sqlQuery);
                    if(foundError) return null;
                    break;
                default:
                    error("Valid clause not found");
                    break;
            }
        }
        if(!isSubQuery) {
            notify(sqlQuery);
        }
        foundError = false;
        return sqlQuery;
    }

    private void generateSelectClause(ListIterator<String> listIterator, SQLQuery sqlQuery) {
        SelectClause selectClause = new SelectClause(sqlQuery);
        boolean validSelect = false;
        while (listIterator.hasNext()) {
            String next = listIterator.next();
            next = next.replaceAll(",", "");
            boolean isKeyword = SQLKeyword.checkKeyword(next);
            if (next.matches("[a-z0-9_]+\\([a-z0-9_]+\\)")) {
                String[] aggregateFunction = next.split("[(,)]");
                selectClause.addColumn(new Column(aggregateFunction[1], aggregateFunction[0]));
            } else if (!isKeyword && (next.matches("[a-z0-9_*]+") || next.matches("[a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+"))) {
                if(next.equals("*")) {
                    if(!validSelect) {
                        selectClause.setStar(true);
                        validSelect = true;
                        break;
                    } else {
                        error("SELECT contains something besides *");
                        return;
                    }
                }
                selectClause.addColumn(new Column(next));
            } else {
                listIterator.previous();
                break;
            }
            validSelect = true;
        }
        if(!validSelect) {
            error("SELECT clause not valid");
            return;
        }
    }

    private void generateFromClause(ListIterator<String> listIterator, SQLQuery sqlQuery) {
        if(!listIterator.hasNext()) error("FROM clause not valid");
        String next = listIterator.next();
        FromClause fromClause = new FromClause(sqlQuery);
        if (next.matches("[a-z0-9_]+\\.[a-z0-9_]+")) {
            Table table = new Table(next);
            fromClause.setTable(table);
        } else error("FROM not followed by TABLE");
        while (listIterator.hasNext()) {
            if(!listIterator.next().matches("join")) {
                listIterator.previous();
                break;
            }
            if(!listIterator.hasNext()) error("FROM clause not valid");
            next = listIterator.next();
            if (next.matches("[a-z0-9_]+\\.[a-z0-9_]+")) {
                Table table = new Table(next);
                if(!listIterator.hasNext()) error("FROM clause not valid");
                next = listIterator.next();
                if (next.equals("using")) {
                    if(!listIterator.hasNext()) error("FROM clause not valid");
                    next = listIterator.next();
                    if (next.matches("\\([a-z0-9_]+\\)") || next.matches("\\([a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+\\)")) {
                        fromClause.addJoin(new JoinCondition(table, new Column(next.replaceAll("[()]", ""))));
                    } else error("USING not followed by valid condition");
                } else if (next.equals("on")) {
                    if(!listIterator.hasNext()) error("FROM clause not valid");
                    next = listIterator.next();
                    if (next.matches("\\([a-z0-9_]+=[a-z0-9_]+\\)") || next.matches("\\([a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+=[a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+\\)")) {
                        next = next.replaceAll("[()]", "");
                        String[] columns = next.split("=");
                        fromClause.addJoin(new JoinCondition(table, new Column(columns[0]), new Column(columns[1])));
                        System.out.println(fromClause);
                    } else error("ON not followed by valid condition");
                } else error("JOIN not followed by valid condition (ON/USING)");
            } else error("JOIN not followed by valid TABLE name");
        }
    }
    private void generateWhereClause(ListIterator<String> listIterator, SQLQuery sqlQuery) {
        WhereClause whereClause = new WhereClause(sqlQuery);
        boolean validCondition = false;
        while(listIterator.hasNext()) {
            String next = listIterator.next();
            boolean isKeyword = SQLKeyword.checkKeyword(next);
            if (!isKeyword && (next.matches("[a-z0-9_]+") || next.matches("[a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+"))) {
                Column c = new Column(next);
                if (!listIterator.hasNext()) {
                    error("WHERE clause not valid");
                    return;
                }
                next = listIterator.next();
                if (next.equals("like")) {
                    if (!listIterator.hasNext()) {
                        error("WHERE clause not valid");
                        return;
                    }
                    next = listIterator.next();
                    if (next.matches("'[^']+'")) {
                        whereClause.addCondition(new LikeCondition(c, next));
                    } else {
                        error("LIKE doesn't have a valid argument");
                        return;
                    }
                } else if (next.matches("(>)|(<)|(>=)|(<=)|=")) {
                    String operator = next;
                    if (!listIterator.hasNext()) {
                        error("WHERE clause not valid");
                        return;
                    }
                    next = listIterator.next();
                    if (next.equals("(select")) {
                        String ssubQuery = locateSubQuery(listIterator);
                        SQLQuery subQuery = parse(ssubQuery, true);
                        whereClause.addCondition(new RelationCondition(c, operator, subQuery));
                    } else if (next.matches("[0-9]+")) {
                        whereClause.addCondition(new RelationCondition(c, operator, Integer.valueOf(next)));
                    } else {
                        error("Relation condition doesn't have a valid argument");
                        return;
                    }
                } else if (next.equals("in")) {
                    if (!listIterator.hasNext()) {
                        error("WHERE clause not valid");
                        return;
                    }
                    next = listIterator.next();
                    if (next.equals("(select")) {
                        String ssubQuery = locateSubQuery(listIterator);
                        SQLQuery subQuery = parse(ssubQuery, true);
                        InCondition inCondition = new InCondition(c, true);
                        inCondition.addValue(subQuery);
                        whereClause.addCondition(inCondition);
                    } else if (next.matches("\\([0-9]+(,[0-9]+)*\\)")) {
                        next = next.replaceAll("[()]", "");
                        List<String> values = List.of(next.split(","));
                        InCondition inCondition = new InCondition(c, false);
                        for (String value : values)
                            inCondition.addValue(Integer.valueOf(value));
                        whereClause.addCondition(inCondition);
                    } else if (next.matches("\\('[a-z0-9_]+'\\)")) {
                        next = next.replaceAll("[()]", "");
                        List<String> values = List.of(next.split(","));
                        InCondition inCondition = new InCondition(c, false);
                        for (String value : values)
                            inCondition.addValue(value);
                        whereClause.addCondition(inCondition);
                    } else {
                        error("IN doesn't have a valid argument");
                        return;
                    }
                } else {
                    error("WHERE isn't followed by valid condition");
                    return;
                }
                validCondition = true;
                if (!listIterator.hasNext()) break;
                next = listIterator.next();
                if (next.equals("and") || next.equals("or")) {
                    whereClause.addLogicalOperator(next);
                    validCondition = false;
                } else {
                    listIterator.previous();
                    break;
                }
            } else {
                error("WHERE condition doesn't contain a valid column");
                return;
            }
        }
        if(!validCondition) {
            error("WHERE clause not valid");
            return;
        }
    }

    private void generateGroupClause(ListIterator<String> listIterator, SQLQuery sqlQuery) {
        if (listIterator.hasNext() && listIterator.next().equals("by")) {
            GroupClause groupClause = new GroupClause(sqlQuery);
            boolean validGroup = false;
            while (listIterator.hasNext()) {
                String next = listIterator.next();
                next = next.replaceAll(",", "");
                boolean isKeyword = SQLKeyword.checkKeyword(next);
                if (!isKeyword && (next.matches("[a-z0-9_]+") || next.matches("[a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+"))) {
                    groupClause.addGroupColumn(new Column(next));
                } else {
                    listIterator.previous();
                    break;
                }
                validGroup = true;
            }
            if(!validGroup) error("GROUP BY clause not valid");
        } else error("GROUP not followed by BY");
    }
    private void generateOrderClause(ListIterator<String> listIterator, SQLQuery sqlQuery) {
        boolean validOrder = false;
        if (listIterator.hasNext() && listIterator.next().equals("by")) {
            OrderClause orderClause = new OrderClause(sqlQuery);
            while(listIterator.hasNext()) {
                validOrder = false;
                String next = listIterator.next();
                if (next.matches("[a-z0-9_]+") || next.matches("[a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+")) {
                    Column column = new Column(next);
                    if(!listIterator.hasNext()) {
                        error("ORDER BY clause not valid");
                        return;
                    }
                    next = listIterator.next().replaceAll(",", "");
                    if (next.equals("asc") || next.equals("desc")) {
                        orderClause.addOrder(column, next);
                    } else {
                        error("ORDER BY doesn't have a valid order type");
                        return;
                    }
                } else {
                    error("ORDER BY doesn't have a valid column to sort by");
                    return;
                }
                validOrder = true;
            }
        } else error("ORDER not followed by BY");
        if(!validOrder) error("ORDER not valid");
    }

    private String locateSubQuery(ListIterator<String> listIterator) {
        int lp = 1;
        int rp = 0;
        StringBuilder result = new StringBuilder(listIterator.previous().replaceAll("\\(",""));
        listIterator.next();
        while (listIterator.hasNext() && lp > rp) {
            String next = listIterator.next();
            if(next.contains("("))
                lp++;
            if(next.contains(")")) {
                rp++;
                if(lp == rp)
                    next = next.replaceAll("\\)", "");
            }
            result.append(" ").append(next);
        }
        return result.toString();
    }

    public void error(String message) {
        MainFrame.getInstance().getAppCore().getMessageGenerator().getMessage(message);
        foundError = true;
    }

    @Override
    public void addSub(ISubscriber sub) {
        this.subs.add(sub);
    }

    @Override
    public void removeSub(ISubscriber sub) {
        this.subs.remove(sub);
    }

    @Override
    public void notify(Object notification) {
        for(ISubscriber sub : subs){
            sub.update(notification);
        }
    }
}
