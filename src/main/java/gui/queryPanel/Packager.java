package gui.queryPanel;

import com.mongodb.client.MongoCursor;
import data.Row;
import database.mongo.MongoConnection;
import gui.MainFrame;
import gui.table.TableModel;
import observer.ISubscriber;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Packager implements ISubscriber {

    private TableModel filteredModel;
    private List<Row> rows;
    private  boolean errorFlag = false;
    private List<String> tables;

    public Packager() {
        this.filteredModel = new TableModel();
        rows = new ArrayList<>();
        tables = new ArrayList<>();
        tables.add("countries");
        tables.add("departments");
        tables.add("employees");
        tables.add("job_history");
        tables.add("jobs");
        tables.add("locations");
        tables.add("regions");
        tables.add("substring");
    }

    public void setFilteredData(){
        // dobijam listu dokumenata i treba od toga da nparavim redove
        filteredModel.setRows(rows);
    }

    public TableModel getFilteredModel() {
        return filteredModel;
    }

    @Override
    public void update(Object notification) {

        errorFlag = false;

        rows = new ArrayList<>();

        MongoCursor<Document> cursor = (MongoCursor<Document>) notification;

        List<Document> documents = new ArrayList<>();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            documents.add(document);
        }

        cursor.close();
        MongoConnection.closeConnection();

        if(!documents.isEmpty()) {
            List<String> columns = new ArrayList<>();
            // documents.get(0).keySet()
            for(String string : documents.get(0).keySet()) {
                if(tables.contains(string)) {
                    Document doc = (Document) documents.get(0).get(string);
                    for(String cols : doc.keySet()) {
                        columns.add(string + "." + cols);
                    }
                } else {
                    columns.add(string);
                }
            }
            for (Document document : documents) {
                Row row = new Row();
                for (String column : columns) {
                    Object value;
                    if(column.contains(".")){
                        String[] col = column.split("\\.");
                        value = ((Document) document.get(col[0])).get(col[1]);
                    } else {
                        value = document.get(column);
                    }
                    row.addField(column, value);
                }

                rows.add(row);
            }
        } else {
            MainFrame.getInstance().getAppCore().getMessageGenerator().getMessage("List of documents is empty (probably a typo)");
            errorFlag = true;
            return;
        }

    }

    public boolean isErrorFlag() {
        return errorFlag;
    }
}
