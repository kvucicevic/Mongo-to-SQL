package start;

import adapter.QueryAdapter;
import adapter.QueryAdapterImplementation;
import database.mongo.MongoConnection;
import database.settings.Settings;
import database.settings.SettingsImplementation;
import gui.queryPanel.Packager;
import gui.table.TableModel;
import message.MessageGenerator;
import message.MessageGeneratorImplementation;
import parser.Parser;
import parser.sql.SQLParser;
import utils.Constants;
import validator.Validator;

public class AppCore {

    private TableModel tableModel;
    private MongoConnection mongoConnection;
    private Settings settings;
    private Parser parser;
    private Validator validator;
    private QueryAdapter queryAdapter;
    private MessageGenerator messageGenerator;
    private Packager packager;

    public AppCore() {
        this.tableModel = new TableModel();
        this.settings = initSettings();
        this.mongoConnection = new MongoConnection(this.settings);
        this.parser = new SQLParser();
        this.messageGenerator = new MessageGeneratorImplementation();
        this.validator = new Validator();
        this.queryAdapter = new QueryAdapterImplementation();
        this.packager = new Packager();
        parser.addSub(validator);
        validator.addSub(queryAdapter);
        queryAdapter.addSub(packager);
    }

    private Settings initSettings(){
        Settings settingsImpl = new SettingsImplementation();
        settingsImpl.addParameter("134.209.239.154", Constants.IP);
        settingsImpl.addParameter("bp_tim68", Constants.DATABASE);
        settingsImpl.addParameter("writer", Constants.USERNAME);
        settingsImpl.addParameter("ItjYs8WD4VajT56A", Constants.PASSWORD);
        return settingsImpl;
    }

    public void initialiseData(String fromTable){   //inicijalno ucitavanje podataka iz tabele
        tableModel.setRows(mongoConnection.readData(fromTable));
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public Parser getParser() {
        return parser;
    }

    public MessageGenerator getMessageGenerator() {
        return messageGenerator;
    }

    public QueryAdapter getQueryAdapter() {
        return queryAdapter;
    }

    public Packager getPackager() {
        return packager;
    }
}
