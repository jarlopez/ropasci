package ropasci.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer implements Runnable {
    private final ServerSocket serverSocket;
    private final Supervisor supervisor;
    private final String serverName;
    private Thread thread;
    private boolean stop;

    public PeerServer(ServerSocket serverSocket, String serverName, Supervisor supervisor) {
        this.serverSocket = serverSocket;
        this.serverName = serverName;
        this.supervisor = supervisor;
        this.stop = false;
    }

    public void startAsThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(!stop) {
            try {
                final Socket socket = serverSocket.accept();
                Peer peer = new Peer(socket, supervisor.getId(), serverName);
                supervisor.addPeer(peer);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }
        System.out.println("Done loopin");
        try {
            serverSocket.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    public void shutdown() {
        stop = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            // Ignore, we're shutting down for good
        }
        thread.interrupt();
    }
}
