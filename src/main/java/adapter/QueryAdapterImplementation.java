package adapter;

import com.mongodb.client.MongoCursor;
import database.SQL.Column;
import database.SQL.LogicalOperator;
import database.SQL.OrderType;
import database.SQL.SQLQuery;
import database.SQL.clause.*;
import database.SQL.condition.*;
import database.mongo.MongoQuery;
import observer.ISubscriber;
import org.bson.Document;

import java.util.*;

public class QueryAdapterImplementation implements QueryAdapter {

    private SQLQuery query;
    private List<ISubscriber> subs;
    private String table;
    private Document selectConverted;
    private List<Document> fromConverted;
    private List<Document> whereConverted;
    private Document groupConverted;
    private Document orderConverted;

    public QueryAdapterImplementation() {
        this.subs = new LinkedList<>();
    }

    @Override
    public void queryConverter() { /// this is ging to convert sql queries to Mongo ones
        convertParameters();
        MongoQuery mongoQuery = new MongoQuery(table);
        map(mongoQuery);
        System.out.println(mongoQuery);
        MongoCursor<Document> cursor = mongoQuery.runQuery();  /// ovo je logicno, a ne mongoQuery.runQuery pa notify(query)
        notify(cursor);
    }

    @Override
    public void update(Object notification) {
        this.fromConverted = new LinkedList<>();
        this.whereConverted = new LinkedList<>();
        query = (SQLQuery) notification;
        queryConverter();
    }

    public void convertParameters() {
        convertSelect();
        convertFrom();
        convertWhere(query,false);
        convertGroup();
        convertOrder();
    }

    private String convertWhere(SQLQuery sqlQuery, boolean isSubQuery) {
        for(SQLClause clause : sqlQuery.getClauses()) {
            if(clause instanceof WhereClause) {
                WhereClause whereClause = (WhereClause) clause;
                StringBuilder match = new StringBuilder("{$match:");
                StringBuilder sub = new StringBuilder();
                ListIterator<LogicalOperator> listIterator = whereClause.getLogicalOperators().listIterator();
                for (WGCondition condition : whereClause.getConditionList()) {
                    match.append("{");
                    if(listIterator.hasNext()) {
                        match.append("$").append(listIterator.next().name().toLowerCase()).append(":[{");
                    }
                    if(condition instanceof InCondition) {
                        InCondition inCondition = (InCondition) condition;
                        if(inCondition.isSubQuery()) {
                            SQLQuery subQuery = (SQLQuery) inCondition.getValues().get(0);
                            SelectClause subSelectClause = (SelectClause) subQuery.getClauses().get(0);
                            FromClause subFromClause = (FromClause) subQuery.getClauses().get(1);
                            sub.append("{$lookup:{");
                            sub.append("from:\"").append(subFromClause.getTable().getTableName().split("\\.")[1]).append("\"");
                            String localColumn = (inCondition.getConditionColumn().getColumnName().contains(".")) ? inCondition.getConditionColumn().getColumnName().split("\\.")[0] : inCondition.getConditionColumn().getColumnName();
                            String foreignColumn = (subSelectClause.getColumns().get(0).getColumnName().contains(".")) ? subSelectClause.getColumns().get(0).getColumnName().split("\\.")[0] : subSelectClause.getColumns().get(0).getColumnName();
                            sub.append(",localField:").append("\"").append(localColumn).append("\"");
                            sub.append(",foreignField:").append("\"").append(foreignColumn).append("\"");;
                            sub.append("as: \"subquery\"");
                            sub.append("}}");
                            whereConverted.add(Document.parse(sub.toString()));
                            sub = new StringBuilder("{ $unwind:\"$subquery\"}");
                            whereConverted.add(Document.parse(sub.toString()));
                            String result = convertWhere(subQuery,true);
                            result = result.replace("{$match:{","").replaceAll("}}$","");
                            match.append(result);
                        } else {
                            if(condition.getConditionColumn().getColumnName().contains(".")) {
                                match.append("\"").append("$").append(condition.getConditionColumn().getColumnName().split("\\.")[1]);
                                match.append(".").append(condition.getConditionColumn().getColumnName().split("\\.")[2]);
                            } else if(isSubQuery) {
                                match.append("\"").append("subquery.").append(condition.getConditionColumn().getColumnName()).append("\":{$");
                            } else {
                                match.append("\"").append(condition.getConditionColumn().getColumnName()).append("\":{$");
                            }
                            match.append("in:[");
                            for(Object value : inCondition.getValues()) {
                                match.append(value.toString()).append(",");
                            }
                            match.deleteCharAt(match.lastIndexOf(","));
                            match.append("]");
                            match.append("},");
                        }
                    } else if(condition instanceof RelationCondition) {
                        RelationCondition relationCondition = (RelationCondition) condition;
                        if(relationCondition.getReferenceValue() instanceof SQLQuery) {
                            /* SQLQuery subQuery = (SQLQuery) relationCondition.getReferenceValue();
                            SelectClause subSelectClause = (SelectClause) subQuery.getClauses().get(0);
                            FromClause subFromClause = (FromClause) subQuery.getClauses().get(1);
                            sub.append("{ $lookup: {");
                            sub.append("from:\"").append(subFromClause.getTable().getTableName().split("\\.")[1]).append("\"");
                            String localColumn = (relationCondition.getConditionColumn().getColumnName().contains(".")) ? relationCondition.getConditionColumn().getColumnName().split("\\.")[0] : relationCondition.getConditionColumn().getColumnName();
                            String foreignColumn = (subSelectClause.getColumns().get(0).getColumnName().contains(".")) ? subSelectClause.getColumns().get(0).getColumnName().split("\\.")[0] : subSelectClause.getColumns().get(0).getColumnName();
                            sub.append(",localField:").append("\"").append(localColumn).append("\"");
                            sub.append(",foreignField:").append("\"").append(foreignColumn).append("\"");;
                            sub.append("as: \"subquery\"");
                            sub.append("}},{ $unwind:\"$subquery\"},"); */
                        } else {
                            if(condition.getConditionColumn().getColumnName().contains(".")) {
                                match.append("\"").append("$").append(condition.getConditionColumn().getColumnName().split("\\.")[1]);
                                match.append(".").append(condition.getConditionColumn().getColumnName().split("\\.")[2]);
                            } else if(isSubQuery) {
                                match.append("\"").append("subquery.").append(condition.getConditionColumn().getColumnName()).append("\":{$");
                            } else {
                                match.append("\"").append(condition.getConditionColumn().getColumnName()).append("\":{$");
                            }
                            match.append(relationCondition.getRelationConditionType().name().toLowerCase()).append(":");
                            match.append(relationCondition.getReferenceValue().toString()).append("}");
                        }
                    } else {
                        if(condition.getConditionColumn().getColumnName().contains(".")) {
                            match.append("\"").append("$").append(condition.getConditionColumn().getColumnName().split("\\.")[1]);
                            match.append(".").append(condition.getConditionColumn().getColumnName().split("\\.")[2]);
                        } else if(isSubQuery) {
                            match.append("\"").append("subquery.").append(condition.getConditionColumn().getColumnName()).append("\":/^");
                        } else {
                            match.append("\"").append(condition.getConditionColumn().getColumnName()).append("\":/^");
                        }
                        match.append(((LikeCondition)condition).getReferenceValue().replace("%",".*").replace("_",".").replace("'",""));
                        match.append("$/i");
                    }
                    match.append("},");
                }
                match.deleteCharAt(match.lastIndexOf(","));
                for(int i = 0; i < whereClause.getLogicalOperators().size(); i++) {
                    match.append("]}");
                }
                match.append("}");
                System.out.println(match);
                if(!isSubQuery) {
                    whereConverted.add(Document.parse(match.toString()));
                    return null;
                } else {
                    return match.toString();
                }
            }
        }
        if(!isSubQuery) whereConverted = null;
        return null;
    }

    private void convertGroup() {
        for(SQLClause clause : query.getClauses()) {
            if(clause instanceof GroupClause) {
                GroupClause groupClause = (GroupClause) clause;
                StringBuilder group = new StringBuilder("{ $group: { _id: {");
                for(Column c : groupClause.getGroupColumns()) {
                    if(c.getColumnName().contains(".")) {
                        group.append(c.getColumnName().replace(".", "_")).append(":\"");
                        group.append("$").append(c.getColumnName().split("\\.")[1]).append(".").append(c.getColumnName().split("\\.")[2]).append("\",");
                    } else {
                        group.append(c.getColumnName()).append(":\"");
                        group.append("$").append(c.getColumnName()).append("\",");
                    }
                }
                group.deleteCharAt(group.lastIndexOf(",")).append("},");
                for(Column c : ((SelectClause) query.getClauses().get(0)).getColumns()) {
                    if(c.isAggregate()) {
                        if(c.getColumnName().contains(".")) {
                            group.append("\"").append(c.getAggregateFunction().name().toLowerCase());
                            group.append("_").append(c.getColumnName().replace(".","_")).append("\":");
                            group.append("{ $").append(c.getAggregateFunction().name().toLowerCase()).append(":\"");
                            group.append("$").append(c.getColumnName().split("\\.")[1]).append(".").append(c.getColumnName().split("\\.")[2]).append("\"},");
                        } else {
                            group.append("\"").append(c.getAggregateFunction().name().toLowerCase());
                            group.append("_").append(c.getColumnName()).append("\":");
                            group.append("{ $").append(c.getAggregateFunction().name().toLowerCase()).append(":\"");
                            group.append("$").append(c.getColumnName()).append("\"},");
                        }
                    } else {
                        if(c.getColumnName().contains(".")) {
                            group.append("\"").append(c.getColumnName().replace(".","_")).append("\":");
                            group.append("{ $first").append(":\"");
                            group.append("$").append(c.getColumnName().split("\\.")[1]).append(".").append(c.getColumnName().split("\\.")[2]).append("\"},");
                        } else {
                            group.append("\"").append(c.getColumnName()).append("\":");
                            group.append("{ $first").append(":\"");
                            group.append("$").append(c.getColumnName()).append("\"},");
                        }
                    }
                }
                group.deleteCharAt(group.lastIndexOf(",")).append("}}");
                groupConverted = Document.parse(group.toString());
                return;
            }
        }
        groupConverted = null;
    }

    private void convertFrom() {
        FromClause fromClause = (FromClause) query.getClauses().get(1);
        table = fromClause.getTable().getTableName().split("\\.")[1];
        if(fromClause.isHasJoins()) {
            StringBuilder joins = new StringBuilder();
            for (JoinCondition joinCondition : fromClause.getJoins()) {
                joins.append("{ $lookup :{");
                joins.append("from:\"").append(joinCondition.getJoinTable().getTableName().split("\\.")[1]).append("\"");
                String conditionColumn = (joinCondition.getConditionColumn().getColumnName().contains(".")) ? joinCondition.getConditionColumn().getColumnName().split("\\.")[0] : joinCondition.getConditionColumn().getColumnName();
                joins.append(",localField:").append("\"").append(conditionColumn).append("\"");
                if(joinCondition.getJoinConditionType() == JoinConditionType.ON) {
                    String conditionColumnOn = (joinCondition.getConditionColumnOn().getColumnName().contains(".")) ? joinCondition.getConditionColumnOn().getColumnName().split("\\.")[0] : joinCondition.getConditionColumnOn().getColumnName() ;
                    joins.append(",foreignField:").append("\"").append(conditionColumnOn).append("\"");
                } else {
                    joins.append(",foreignField:").append("\"").append(conditionColumn).append("\"");;
                }
                joins.append("as: \"").append(joinCondition.getJoinTable().getTableName().split("\\.")[1]).append("\"");
                joins.append("}}");
                fromConverted.add(Document.parse(joins.toString()));
                joins = new StringBuilder();
                joins.append("{ $unwind:\"$").append(joinCondition.getJoinTable().getTableName().split("\\.")[1]).append("\"}");
                fromConverted.add(Document.parse(joins.toString()));
            }
        }
    }

    private void convertSelect() {
        SelectClause selectClause = (SelectClause) query.getClauses().get(0);
        if(selectClause.isStar()) {
            selectConverted = null;
            return;
        }
        StringBuilder json = new StringBuilder("{ $project : {");
        for(Column c : selectClause.getColumns()) {
            if(!c.getColumnName().contains(".")) {
                if (c.isAggregate()) {
                    json.append("\"").append(c.getAggregateFunction().name().toLowerCase()).append("_").append(c.getColumnName()).append("\": 1,");
                } else {
                    json.append("\"").append(c.getColumnName()).append("\": 1,");
                }
            } else {
                json.append("\"").append(c.getColumnName().split("\\.")[1]).append(".");
                if (c.isAggregate()) {
                    json.append(c.getAggregateFunction().name().toLowerCase()).append("_").append(c.getColumnName().split("\\.")[2]).append("\": 1,");
                } else {
                    json.append(c.getColumnName().split("\\.")[2]).append("\": 1,");
                }
            }
        }
        json.append("_id:0}}");
        selectConverted = Document.parse(json.toString());
    }

    public void convertOrder() {
        for(SQLClause clause : query.getClauses()) {
            if(clause instanceof OrderClause) {
                StringBuilder order = new StringBuilder("{ $sort:{");
                OrderClause orderClause = (OrderClause) clause;
                for(Map.Entry<Column, OrderType> key : orderClause.getOrderColumns().entrySet()) {
                    if(key.getValue() == OrderType.ASC) {
                        order.append("\"" + key.getKey().getColumnName() + "\": 1,");
                    } else {
                        order.append("\"" + key.getKey().getColumnName() + "\": -1,");
                    }
                }
                order.deleteCharAt(order.lastIndexOf(","));
                order.append("}}");
                System.out.println(order);
                orderConverted = Document.parse(order.toString());
                return;
            }
        }
        orderConverted = null;
    }

    public void map(MongoQuery mongoQuery) {
        if(whereConverted != null) {
            if (!whereConverted.isEmpty()) {
                for (Document doc : whereConverted) {
                    mongoQuery.addJsonQuery(doc);
                }
            }
        }
        if(!fromConverted.isEmpty()) {
            for (Document doc : fromConverted) {
                mongoQuery.addJsonQuery(doc);
            }
        }
        if(groupConverted != null) mongoQuery.addJsonQuery(groupConverted);
        if(selectConverted != null) mongoQuery.addJsonQuery(selectConverted);
        if(orderConverted != null) mongoQuery.addJsonQuery(orderConverted);
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
        for(ISubscriber sub : subs) {
            sub.update(notification);
        }
    }
}
