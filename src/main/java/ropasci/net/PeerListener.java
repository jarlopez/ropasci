package ropasci.net;

public interface PeerListener {
    void onConnected(Peer peer);
    void onDisconnected(Peer peer);
    void onReceiveCommand(Peer peer, String data);
    void onReceiveCommand(Peer peer, String operation, String data);
}
