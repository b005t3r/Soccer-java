package gui;

import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import com.jgoodies.plaf.plastic.theme.SkyGreen;
import com.jgoodies.plaf.plastic.theme.Silver;
import soccer.core.GamePanel;
import soccer.network.ClientControler;
import soccer.network.ServerControler;
import soccer.single.HotSeatControler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SoccerFrame extends JFrame implements ActionListener, DialogConstants {

    private JMenuBar  menuBar;
    private JMenu     gameMenu;
    private JMenu     singlePCGame;
    private JMenu     networkGame;
    private JMenuItem newHotSeatGame;
    private JMenuItem newHostGame;
    private JMenuItem newClientGame;
    private JMenuItem exit;

    private HotSeatDialog hotSeatDialog;
    private HostDialog hostDialog;
    private JoinDialog joinDialog;

    private GamePanel gamePanel;

    public SoccerFrame() throws HeadlessException {
        super("Soccer v 1.0rc");

        gamePanel = new GamePanel(new HotSeatControler("Player 1", "Player 2"), this);
        gamePanel.setPlayer1("Player 1");
        gamePanel.setPlayer2("Player 2");

        Container cont = getContentPane();
        cont.add(gamePanel);

        newHotSeatGame = new JMenuItem("New HotSeat game");
        newHotSeatGame.addActionListener(this);

        exit = new JMenuItem("Exit");
        exit.addActionListener(this);

        singlePCGame = new JMenu("Single PC game");
        singlePCGame.add(newHotSeatGame);

        newHostGame = new JMenuItem("Host game...");
        newHostGame.addActionListener(this);

        newClientGame = new JMenuItem("Join game...");
        newClientGame.addActionListener(this);

        networkGame = new JMenu("Network game");
        networkGame.add(newHostGame);
        networkGame.add(newClientGame);

        gameMenu = new JMenu("Game");
        gameMenu.add(singlePCGame);
        gameMenu.add(networkGame);
        gameMenu.add(exit);

        menuBar = new JMenuBar();
        menuBar.add(gameMenu);

        setJMenuBar(menuBar);

        hotSeatDialog = new HotSeatDialog(this);
        hostDialog = new HostDialog(this);
        joinDialog = new JoinDialog(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == newHotSeatGame) {
            hotSeatDialog.show();
            if(hotSeatDialog.getReturnStatus() == RET_OK)
                gamePanel.restart(new HotSeatControler(
                        hotSeatDialog.getPlayer1Name(),
                        hotSeatDialog.getPlayer2Name())
                );
        }
        else if(e.getSource() == newHostGame) {
            hostDialog.show();
            if(hostDialog.getReturnStatus() == RET_OK)
                gamePanel.restart(new ServerControler(
                        hostDialog.getPort(),
                        hostDialog.isServerFirst(),
                        hostDialog.getPlayerName())
                );
        }
        if(e.getSource() == newClientGame) {
            joinDialog.show();
            if(joinDialog.getReturnStatus() == RET_OK)
                gamePanel.restart(new ClientControler(
                        joinDialog.getAddress(),
                        joinDialog.getPort(),
                        joinDialog.getPlayerName()));
        }
        else if(e.getSource() == exit) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
       Plastic3DLookAndFeel.setMyCurrentTheme(new Silver());
       try {
          UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
       } catch (Exception e) {}

        SoccerFrame sf = new SoccerFrame();
        sf.show();
    }
}
