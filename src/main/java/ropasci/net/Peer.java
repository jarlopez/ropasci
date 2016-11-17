package ropasci.net;

import ropasci.net.protocol.RPSMessage;
import ropasci.net.protocol.RecvHandler;
import ropasci.net.protocol.SendHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class Peer {
    private static final Logger log = Logger.getLogger(Peer.class.getName());

    public static final String ALREADY_CONNECTED = "Peer has already connected";

    private String id; // Our ID
    private String connectedPeerId; // ID of connected peer
    private String username; // Username, if provided

    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private SendHandler out;
    private RecvHandler in;
    private Connection connection;

    private int port;
    private InetAddress host;

    public Peer(InetAddress host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public Peer(InetAddress host, int port, String id, String username) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.username = username;
    }

    public Peer(Socket socket, String id) throws IOException {
        this.id = id;
        this.host = socket.getInetAddress();
        this.port = socket.getPort();
        setupDataStreams(socket);
        handshake();
    }

    public int getPort() {
        return port;
    }

    public InetAddress getHost() {
        return host;
    }

    public String getId() {
        return id;
    }

    public String getConnectedPeerId() {
        return connectedPeerId;
    }

    public void connect(PeerListener listener) {
        log.info("Attempting to connect peer:" + id + " to " + host.getHostAddress() + ":" + port);
        try {
            if (dataIn == null || dataOut == null) {
                Socket socket = new Socket(host, port);
                setupDataStreams(socket);
                connectedPeerId = handshake();
                if (id.equals(connectedPeerId)) {
                    throw new IOException("Connecting to self is not permitted!");
                }
            }
            in = new RecvHandler(this, dataIn);
            out = new SendHandler(this, dataOut);

            Thread inputHandler = new Thread(in);
            Thread outputHandler = new Thread(out);
            connection = new Connection(this, listener, in, out);
            inputHandler.start();
            outputHandler.start();

            // Let our listener know
            listener.onConnected(this);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onDisconnected(this);
        }
    }

    private String handshake() throws IOException {
        int dataLength = 1; // For 'handshake' byte
        byte[] idBytes = id.getBytes("UTF-8");
        dataLength += idBytes.length;

        dataOut.writeInt(dataLength);
        dataOut.writeByte(RPSMessage.HANDSHAKE);
        dataOut.write(idBytes);
        dataOut.flush();

        // Receive handshake response
        int responseLength = dataIn.readInt();
        byte responseType = dataIn.readByte();
        if (responseType != RPSMessage.HANDSHAKE) {
            throw new IOException("Invalid response type during handshake");
        }
        byte[] peerIdBytes = new byte[responseLength - 1];
        int readLength = dataIn.read(peerIdBytes);
        String peerId = new String(peerIdBytes, "UTF-8");
        log.info("Handshake completed with peer:" + peerId);
        this.connectedPeerId = peerId;
        return peerId;
    }

    private void setupDataStreams(Socket socket) throws IOException {
        dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            connection.in.disconnect();
            connection.out.disconnect();
            connection = null;
        }
    }
}
