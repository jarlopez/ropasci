package main.java.com.rps.net;

public interface PeerListener {
    void onConnected(Peer peer);
    void onDisconnected(Peer peer);
    void onReceiveCommand(Peer peer, String data);
}
