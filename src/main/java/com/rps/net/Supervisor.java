package main.java.com.rps.net;

import ropasci.net.protocol.RPSMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Maintains list of active peer connections and exposes peer management methods
 */
public class Supervisor implements PeerListener {
    private static final Logger log = Logger.getLogger(Supervisor.class.getName());

    private final List<Peer> peers;
    private SupervisorListener listener;
    private String id; // ID of this node

    public Supervisor(SupervisorListener listener, String id) {
        this.listener = listener;
        this.id = id;
        this.peers = Collections.synchronizedList(new ArrayList<Peer>());
    }

    @Override
    public void onConnected(Peer peer) {
        log.info("Peer " + peer.getId() + " has connected");
        peers.add(peer);
        if (listener != null) {
            listener.onPeerConnected(peer);
        }
    }

    @Override
    public void onDisconnected(Peer peer) {
        log.info("Peer " + peer.getId() + " has disconnected");
        peers.remove(peer);
        if (listener != null) {
            listener.onPeerDisconnected(peer);
        }
    }

    @Override
    public void onReceiveCommand(Peer peer, String data) {
        log.info("Received command from peer " + peer.getId() + ": " + data);
        if (listener != null) {
            listener.onReceiveCommand(peer, data);
        }
    }

    public String getId() {
        return id;
    }

    public void addPeer(Peer peer) {
        log.info("Creating thread for connecting a peer");
        PeerListener listener = this;
        new Thread(() -> peer.connect(listener)).start();
    }

    public void broadcast(RPSMessage msg) {
        log.info("Broadcasting to all peers: " + msg);
        synchronized (peers) {
            for (Peer peer : peers) {
                peer.getConnection().requestSend(msg);
            }
        }
    }
}
