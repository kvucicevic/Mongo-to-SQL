package database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import data.Row;
import database.settings.Settings;
import org.bson.Document;
import utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoConnection {

    private static Settings settings;
    private static MongoClient mongoClient;


    public MongoConnection(Settings settings) {
        this.settings = settings;
    }

    public static MongoClient getConnection(){

        String ip = (String) settings.getParameter(Constants.IP);
        String userName = (String) settings.getParameter(Constants.USERNAME);
        String password = (String) settings.getParameter(Constants.PASSWORD);
        String database = (String) settings.getParameter(Constants.DATABASE);

        MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(ip, 27017), Arrays.asList(credential));
        System.out.println("MongoDB connection established");

        return mongoClient;
    }

    public static void closeConnection(){
        System.out.println("connection succcesfully closed");
        mongoClient.close();
    }

    public List<Row> readData(String fromTable){

        List<Row> rows = new ArrayList<>();

        getConnection();
        MongoDatabase database = mongoClient.getDatabase("bp_tim68");

        MongoCollection<Document> collection = database.getCollection(fromTable);

        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> documents = new ArrayList<>();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            documents.add(document);
        }

        cursor.close();

        List<String> columns = new ArrayList<>(documents.get(0).keySet());
        for (Document document : documents) {

            Row row = new Row();
            for (String column : columns) {
                Object value = document.get(column);

                row.addField(column, value);
            }

            rows.add(row);
        }


        closeConnection();
        return rows;
    }
}
