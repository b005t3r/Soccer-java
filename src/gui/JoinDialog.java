package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JoinDialog extends JDialog implements ActionListener, DialogConstants {

    private JButton okButton;
    private JButton cancelButton;
    private JTextField playerName;
    private JTextField port;
    private JTextField address;
    private JPanel mainPanel;

    private JFrame owner;
    
    private int returnStatus;

    public JoinDialog(JFrame owner) throws HeadlessException {
        super(owner, true);

        this.owner = owner;

        setTitle("Join");

        playerName.setText("Client");
        port.setText("9877");
        address.setText("localhost");

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

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
        System.out.println("getAddress()    = " + getAddress());

        hide();
    }

    public void show() {
        setLocationRelativeTo(owner);
        super.show();
    }

    public int getReturnStatus() { return returnStatus; }

    public String getPlayerName() { return playerName.getText(); }
    public int    getPort()       { return Integer.parseInt(port.getText()); }
    public String getAddress()    { return address.getText(); }
}
