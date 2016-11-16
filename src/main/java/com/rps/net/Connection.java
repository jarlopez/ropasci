package main.java.com.rps.net;

import ropasci.net.protocol.RPSMessage;
import ropasci.net.protocol.RecvHandler;
import ropasci.net.protocol.SendHandler;

import java.util.logging.Logger;

public class Connection {
    // TODO Migrate out to Peer?
    private static final Logger log = Logger.getLogger(Connection.class.getName());

    private final Peer peer;
    private final PeerListener listener;
    protected final RecvHandler in;
    protected final SendHandler out;

    public Connection(Peer peer, PeerListener listener, RecvHandler in, SendHandler out) {
        this.peer = peer;
        this.listener = listener;
        this.in = in;
        this.out = out;
    }

    public void requestSend(RPSMessage data) {
        log.info("Send requested: " + data);
        out.send(data);
    }

    public void onReceiveCommand(Peer peer, String msg) {
        listener.onReceiveCommand(peer, msg);
    }

    public void onReceive(Peer peer, RPSMessage message) {
        switch (message.getType()) {
            case RPSMessage.HEARTBEAT:
                break;
            case RPSMessage.HANDSHAKE:
                break;
            case RPSMessage.DISCONNECT:
                break;
            case RPSMessage.ACTION:
                break;
        }
    }
}
