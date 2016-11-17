package ropasci.net;

import ropasci.net.protocol.RPSMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Logger;

public class NetworkManager {
    private static final Logger log = Logger.getLogger(NetworkManager.class.getName());

    private final int port;
    private final SupervisorListener listener;
    private final String serverName;
    private Supervisor supervisor;

    private final String id;
    private  ServerSocket serverSocket;
    private PeerServer peerServer;

    public NetworkManager(int port, String serverName, SupervisorListener listener) {
        this.id = UUID.randomUUID().toString();
        if (serverName == null) {
            serverName = id;
        }
        this.serverName = serverName;
        this.port = port;
        this.listener = listener;
    }

    public void setupServer() throws IOException {
        log.info("Setting up server capabilities for peer:" + id);
        serverSocket = new ServerSocket(port);
    }

    public void startNetworking() {
        supervisor = new Supervisor(listener, id);
        peerServer = new PeerServer(serverSocket, serverName, supervisor);
        log.info("Starting peer server thread for peer:" + id);
        peerServer.startAsThread();
    }

    public void addPeer(String host, int port) throws UnknownHostException {
        Peer peer = new Peer(InetAddress.getByName(host), port, id, serverName);
        supervisor.addPeer(peer);
    }

    public void broadcast(RPSMessage msg) {
        supervisor.broadcast(msg);
    }
    public void disconnect() {
        RPSMessage msg = new RPSMessage(RPSMessage.DISCONNECT);
        supervisor.broadcast(msg);
        // Shutdodwn server
        // Clean up connections? Bubble down disconnect() to senders/receivers
    }

    public String getId() {
        return id;
    }
}
