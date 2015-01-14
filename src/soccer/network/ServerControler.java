package soccer.network;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerControler extends NetworkControler implements Runnable {

    private ServerSocket serverSocket;

    private Thread runner;
    private boolean running;

    public ServerControler(int port, boolean goFirst, String playerName) {
        super(playerName);

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {}

        moves = new ArrayList();

        runner = new Thread(this);

        this.goFirst = goFirst;
    }

    public void run() {
        openConnection();

        retrieveName();
        sendName();
        sendGoFirst();

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
        System.out.println("Server zabija watek");
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

    private void sendGoFirst() {
        try {
            dos.writeBoolean(! goFirst);
            dos.flush();
        } catch (IOException e) {}
    }

    protected void openConnection() {
        try {
            socket = serverSocket.accept();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {}
    }

    protected void closeConnection() {
        if(socket != null)
            try {
                serverSocket.close();
                socket.close();
                dos.close();
                dis.close();
            } catch (IOException e) {}
        System.out.println("Serwer zamyka gniazdo");
    }

}
