package gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class HostDialog extends JDialog implements ActionListener, DialogConstants {

    private JRadioButton server;
    private JRadioButton client;
    private JTextField port;
    private JTextField playerName;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    private JFrame owner;

    private int returnStatus;

    public HostDialog(JFrame owner) throws HeadlessException {
        super(owner, true);

        this.owner = owner;

        setTitle("Host");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        ButtonGroup bg = new ButtonGroup();
        bg.add(server);
        bg.add(client);

        port.setText("9877");
        playerName.setText("Server");

        returnStatus = RET_CANCEL;

        setContentPane(mainPanel);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();

             if(source.equals(okButton))     returnStatus = RET_OK;
        else if(source.equals(cancelButton)) returnStatus = RET_CANCEL;

        System.out.println("getPlayerName() = " + getPlayerName());
        System.out.println("getPort()       = " + getPort());
        System.out.println("isServerFirst() = " + isServerFirst());

        hide();
    }

    public void show() {
        setLocationRelativeTo(owner);
        super.show();
    }

    public int getReturnStatus() { return returnStatus; }

    public String  getPlayerName() { return playerName.getText(); }
    public int     getPort()       { return Integer.parseInt(port.getText()); }
    public boolean isServerFirst() { return server.isSelected(); }
}
