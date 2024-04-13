package validator;

import database.SQL.Column;
import database.SQL.SQLQuery;
import database.SQL.clause.*;
import gui.MainFrame;
import observer.IPublisher;
import observer.ISubscriber;

import java.util.ArrayList;
import java.util.List;

public class Validator implements ISubscriber, IPublisher, IValidator {

    private SQLQuery query;
    private List<ISubscriber> subs = new ArrayList<>();

    @Override
    public boolean validate(){
        if(!containsSelect()){
            error("SELECT clause missing");
            return false;
        }
        if(!containsFrom()){
            error("FROM clause missing");
            return false;
        }
        if(!aggAndGroup()) {
            error("There are columns which are neither aggregated nor grouped");
            return false;
        }
        return true;
    }

    @Override
    public void update(Object notification) {
        query = (SQLQuery) notification;
        if(validate()){
            notify(query);
        }
    }

    public boolean containsSelect(){
        for(SQLClause clause : query.getClauses()){
            if(clause instanceof SelectClause){
                return true;
            }
        }
        return false;
    }

    public boolean containsFrom(){
        for(SQLClause clause : query.getClauses()){
            if(clause instanceof FromClause){
                return true;
            }
        }
        return false;
    }

    public boolean hasAgg() {
        for(Column c : ((SelectClause) query.getClauses().get(0)).getColumns()) {
            if(c.isAggregate()) return true;
        }
        return false;
    }

    public boolean aggAndGroup(){
        if(!hasAgg()) return true;

        GroupClause groupClause = null;
        for(SQLClause clause : query.getClauses()) {
            if(clause instanceof GroupClause) {
                groupClause = (GroupClause) clause;
                break;
            }
        }
        if(groupClause == null) return false;

        for(Column c : ((SelectClause) query.getClauses().get(0)).getColumns()){
            if(!c.isAggregate()){
                if(!groupClause.getGroupColumns().contains(c))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void addSub(ISubscriber sub) {
        subs.add(sub);
    }

    @Override
    public void removeSub(ISubscriber sub) {
        subs.remove(sub);
    }

    @Override
    public void notify(Object notification) {
        for(ISubscriber s : subs){
            s.update(notification);
        }
    }

    private void error(String message) {
        MainFrame.getInstance().getAppCore().getMessageGenerator().getMessage(message);
    }
}
