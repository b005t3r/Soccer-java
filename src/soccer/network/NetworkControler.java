package soccer.network;

import soccer.core.Controler;
import soccer.core.GamePanel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public abstract class NetworkControler implements Controler, NetworkConstants {

    public static final int SLEEP_TIME = 500;

    protected Socket socket;
    protected GamePanel gamePanel;

    protected DataInputStream dis;
    protected DataOutputStream dos;

    protected ArrayList moves;
    protected int myHeader;
    protected int retrievedHeader;

    protected boolean goFirst;

    protected String myName;
    protected String hisName;

    protected boolean stoped;

    public NetworkControler(String myName) {
        this.myName = myName;
        moves = new ArrayList();
    }

    public void onMove(int dir) {
        moves.add(new Integer(dir));

        if(gamePanel.getGameResult() != GamePanel.RESULT_GAME_GOES_ON) {
            myHeader = RES_GAME_OVER | DATA_MOVES;
            gamePanel.setLock(true);
            gamePanel.setShowAllowedMoves(false);
            gamePanel.showResult();

            stop();
            wakeUp();
        }
        else if(! gamePanel.hasNextMove()) {
            myHeader = DATA_MOVES;
            wakeUp();
        }
    }

    public void onEnd() {
        myHeader = CONN_CLOSE;
        stop();
        wakeUp();
    }

    public void init(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        lockGamePanel();
        start();
    }

    protected void doOponentsMoves() {
        // odtworzanie ruchow przeciwnika

        gamePanel.switchPlayer();

        for(int i = 0; i < moves.size(); i++) {
            gamePanel.addMove(((Integer) moves.get(i)).intValue());
            try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}
        }
        moves.clear();

        if((retrievedHeader & RES_MASK) == RES_GAME_OVER) {
            stop();
            gamePanel.setLock(true);
            gamePanel.setShowAllowedMoves(false);
            gamePanel.showResult();
        }
        else {
            gamePanel.switchPlayer();
        }
    }

    protected void initMove() {
        if(! goFirst) {
            gamePanel.switchPlayer();

            gamePanel.setPlayer1(hisName);
            gamePanel.setPlayer2(myName);

            retrievePackage();
            doOponentsMoves();
        }
        else {
            gamePanel.setPlayer1(myName);
            gamePanel.setPlayer2(hisName);
        }
    }

    protected void lockGamePanel() {
        gamePanel.setShowAllowedMoves(false);
        gamePanel.setLock(true);
        gamePanel.repaint();
    }

    protected void unlockGamePanel() {
        gamePanel.setShowAllowedMoves(true);
        gamePanel.setLock(false);
        gamePanel.repaint();
    }

    protected void sendName() {
        try {
            dos.writeUTF(myName);
            dos.flush();
        } catch (IOException e) {}
    }

    protected void retrieveName() {
        try {
            hisName = dis.readUTF();
        } catch (IOException e) {}
    }

    protected void sendPackage() {
        try {
            dos.writeInt(myHeader);

            if((myHeader & DATA_MASK) == DATA_MOVES) {
                dos.writeInt(moves.size());
                for(int i = 0; i < moves.size(); i++)
                    dos.writeInt(((Integer) moves.get(i)).intValue());
            }

            dos.flush();
        } catch (IOException e) {}
    }

    protected void retrievePackage() {
        moves.clear();

        try {
            retrievedHeader = dis.readInt();

            if((retrievedHeader & DATA_MASK) == DATA_MOVES) {
                int size = dis.readInt();
                for(int i = 0; i < size; i++)
                    moves.add(new Integer(dis.readInt()));
            }

            if((retrievedHeader & CONN_MASK) == CONN_CLOSE) {
                stop();
            }
        } catch (IOException e) {}
    }

    public void setGoFirst(boolean goFirst) { this.goFirst = goFirst; }

    protected synchronized void sleep()  { try { wait(); } catch (InterruptedException e) {} }
    protected synchronized void wakeUp() { notifyAll(); }

    protected abstract void start();
    protected abstract void stop();
    protected abstract void openConnection();
    protected abstract void closeConnection();

}
