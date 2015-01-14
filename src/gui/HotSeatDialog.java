package gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class HotSeatDialog extends JDialog implements ActionListener, DialogConstants {

    private JTextField player1Name;
    private JTextField player2Name;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    private JFrame owner;
    
    private int returnStatus;

    public HotSeatDialog(JFrame owner) throws HeadlessException {
        super(owner, true);

        this.owner = owner;

        setTitle("HotSeat");

        player1Name.setText("Player 1");
        player2Name.setText("Player 2");

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

        hide();
    }

    public void show() {
        setLocationRelativeTo(owner);
        super.show();
    }

    public int getReturnStatus() { return returnStatus; }

    public String  getPlayer1Name() { return player1Name.getText(); }
    public String  getPlayer2Name() { return player2Name.getText(); }
}
