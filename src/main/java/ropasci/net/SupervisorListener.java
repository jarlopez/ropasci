package ropasci.net;

public interface SupervisorListener {
    void onPeerConnected(Peer peer);
    void onPeerDisconnected(Peer peer);
    void onReceiveCommand(Peer peer, String data);
    void onNotice(Peer peer, String msg);
}
