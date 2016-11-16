package ropasci;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ropasci.net.NetworkManager;
import ropasci.net.Peer;
import ropasci.net.SupervisorListener;
import ropasci.net.protocol.RPSMessage;

import java.io.IOException;
import java.net.UnknownHostException;

public class Controller implements SupervisorListener {
    @FXML public ListView peersList;

    @FXML public Button connectButton;
    @FXML public TextField listenPort;
    @FXML public Button startServer;
    @FXML public TextField peerPort;
    @FXML public TextField peerHost;
    @FXML public TextArea logArea;
    @FXML public Button rockButton;
    @FXML public Button paperButton;
    @FXML public Button scissorsButton;
    @FXML public Label serverId;

    private NetworkManager manager;

    public void connectPeer(ActionEvent actionEvent) {
        int port = Integer.parseInt(peerPort.getText());
        String host = peerHost.getText();
        try {
            manager.addPeer(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeerConnected(Peer peer) {
        Platform.runLater(() -> {
            peersList.getItems().add(peer.getHost().getHostAddress() + ":" + peer.getPort());
            String line = "[PEER] Added peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n";
            logArea.appendText(line);
        });
    }

    @Override
    public void onPeerDisconnected(Peer peer) {
        Platform.runLater(() -> {
            logArea.appendText("[PEER] Disconnected peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n");
            peersList.getItems().remove(peer.getHost().getHostAddress() + ":" + peer.getPort());
        });
    }

    @Override
    public void onReceiveCommand(Peer peer, String data) {
        // TODO Log
        Platform.runLater(() -> {
            logArea.appendText("[CMD] Received command from peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n" +
                    "\tCommand: " + data + "\n");
        });
    }

    public void startNetworking(ActionEvent actionEvent) {
        int port = Integer.parseInt(listenPort.getText());
        manager = new NetworkManager(port, this);
        try {
            manager.setupServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.startNetworking();
        serverId.setText(manager.getId());
    }

    public void action(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        RPSMessage message = null;
        if (rockButton.equals(source)) {
            message = new RPSMessage(RPSMessage.ACTION, new byte[]{RPSMessage.ACTION_ROCK});
        } else if (paperButton.equals(source)) {
            message = new RPSMessage(RPSMessage.ACTION, new byte[]{RPSMessage.ACTION_PAPER});
        } else if (scissorsButton.equals(source)) {
            message = new RPSMessage(RPSMessage.ACTION, new byte[]{RPSMessage.ACTION_SCISSORS});
        }
        manager.broadcast(message);

    }

    public void heartbeat(ActionEvent actionEvent) {
        RPSMessage message = new RPSMessage(RPSMessage.HEARTBEAT);
        manager.broadcast(message);
    }
}
