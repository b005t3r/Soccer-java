package soccer.network;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientControler extends NetworkControler implements Runnable {

    public static final int SLEEP_TIME = 500;

    private String address;
    private int port;

    private Thread runner;
    private boolean running;

    public ClientControler(String address, int port, String playerName) {
        super(playerName);

        this.address = address;
        this.port = port;

        moves = new ArrayList();

        runner = new Thread(this);
    }

    public void run() {
        openConnection();

        sendName();
        retrieveName();
        retrieveGoFirst();

        initMove();

        while(running) {
            unlockGamePanel();
            sleep();
            lockGamePanel();

            sendPackage();

            if(! stoped) {
                retrievePackage();
                doOponentsMoves();
            }
        }

        closeConnection();
        System.out.println("Client zabija watek");
    }

    protected void start() {
        if(! running) {
            running = true;
            runner.start();
        }
    }

    protected void stop() {
        running = false;
        stoped = true;
    }

    private void retrieveGoFirst() {
        try {
            goFirst = dis.readBoolean();
        } catch (IOException e) {}
    }

    protected final void openConnection() {
        try {
            socket = new Socket(address, port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {}
    }

    protected final void closeConnection() {
        if(socket != null)
            try {
                socket.close();
                dos.close();
                dis.close();
            } catch (IOException e) {}
        System.out.println("Klient zamyka gniazdo");
    }
}
