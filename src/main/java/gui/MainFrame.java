package gui;

import gui.error.ErrorDialog;
import gui.queryPanel.FilterTablePanel;
import message.Message;
import observer.ISubscriber;
import start.AppCore;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements ISubscriber {

    private static MainFrame instance = null;
    private AppCore appCore;
    private JTable table;

    private MainFrame(){

    }

    public static MainFrame getInstance() {
        if(instance == null){
            instance = new MainFrame();
            instance.initialise();
        }
        return instance;
    }

    public void initialise(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());


        JTextArea textArea = new JTextArea(15, 50);  /// staviti jedan panel iza da izgleda lepo
        textArea.setLineWrap(true);
        JButton button = new JButton("Run");
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(button);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(textArea, BorderLayout.NORTH);


        button.addActionListener(e -> {
            appCore.getParser().parse(textArea.getText(), false);
            new FilterTablePanel();
        });

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
        this.table.setModel(appCore.getTableModel());
        appCore.getMessageGenerator().addSub(this);
    }

    public AppCore getAppCore() {
        return appCore;
    }

    @Override
    public void update(Object notification) {
        if(notification instanceof Message) {
            Message message = (Message) notification;
            ErrorDialog errorDialog = new ErrorDialog(this, message);
            errorDialog.setVisible(true);
            //System.out.println(message);
        }
    }
}
