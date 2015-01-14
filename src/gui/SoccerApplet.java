package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SoccerApplet extends JApplet {
    public SoccerApplet() throws HeadlessException {
        JButton startGame = new JButton("Launch Soccer");
        startGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoccerFrame frame = new SoccerFrame();
                frame.show();
            }
        });

        add(startGame);
    }
}
