package gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: booster
 * Date: Nov 13, 2004
 * Time: 3:42:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameResultDialog extends JDialog implements ActionListener {

    private JButton okButton;
    private JLabel text;

    private JFrame owner;
    private JPanel mainPanel;

    public GameResultDialog(JFrame owner) throws HeadlessException {
        super(owner, false);
        this.owner = owner;

        setTitle("Game over");

        okButton.addActionListener(this);

        setContentPane(mainPanel);
    }

    public void actionPerformed(ActionEvent e) { hide(); }

    public void show() {
        pack();
        setLocationRelativeTo(owner);
        super.show();
    }

    public void setText(String t) {
        text.setText(t);
    }
}
