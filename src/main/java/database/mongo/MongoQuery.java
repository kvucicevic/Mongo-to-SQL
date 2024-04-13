package database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import database.Query;
import org.bson.Document;
import utils.Constants;

import java.util.LinkedList;
import java.util.List;

public class MongoQuery implements Query{   /// executor

    private String table;
    private List<Document> jsonQuery;

    public MongoQuery(String table) {
        this.jsonQuery = new LinkedList<>();
        this.table = table;
    }

    @Override
    public MongoCursor<Document> runQuery(){

        MongoClient connection = MongoConnection.getConnection();
        MongoDatabase database = connection.getDatabase(Constants.DATABASE);
        MongoCursor<Document> cursor;
        if(!jsonQuery.isEmpty()) {
            cursor = database.getCollection(table).aggregate(jsonQuery).iterator();
        } else {
            cursor = database.getCollection(table).find(Document.parse("{}")).projection(Document.parse("{}")).iterator();
        }
        //MongoConnection.closeConnection();

        return cursor;
    }

    public void addJsonQuery(Document jsonQuery) {
        this.jsonQuery.add(jsonQuery);
    }

    @Override
    public void update(Object notification) {

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Document doc : jsonQuery) {
            result.append(doc.toJson()).append(",");
        }
        return result.toString();
    }
}
