package gui.error;

import message.Message;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JOptionPane {

    public ErrorDialog(Frame owner, Message message) {
        setBounds(100, 100, 400, 100);
        JOptionPane.showMessageDialog(owner, message.toString());
    }
}
