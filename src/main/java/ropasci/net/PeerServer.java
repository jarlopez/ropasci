package ropasci.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer implements Runnable {
    private final ServerSocket serverSocket;
    private final Supervisor supervisor;
    private Thread thread;
    private boolean stop;

    public PeerServer(ServerSocket serverSocket, Supervisor supervisor) {
        this.serverSocket = serverSocket;
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
                Socket socket = serverSocket.accept();
                Peer peer = new Peer(socket, supervisor.getId());
                supervisor.addPeer(peer);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }
    }
}
