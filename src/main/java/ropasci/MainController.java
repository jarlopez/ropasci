package ropasci;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ropasci.game.RPSGame;
import ropasci.game.RPSStateListener;
import ropasci.game.RPSState;
import ropasci.net.NetworkManager;
import ropasci.net.Peer;
import ropasci.net.SupervisorListener;
import ropasci.net.protocol.RPSMessage;

import java.io.IOException;
import java.net.UnknownHostException;

public class MainController implements SupervisorListener, RPSStateListener
{
    @FXML public Button rockButton;
    @FXML public Button paperButton;
    @FXML public Button scissorsButton;
    @FXML public TextArea logArea;
    @FXML public TextArea scoreArea;
    //TODO
    @FXML public Button heartbeat;

    @FXML public ListView peersList;
    @FXML public Button connectButton;
    @FXML public TextField peerHost;
    @FXML public TextField peerPort;

    @FXML public Label labelUsername;
    @FXML public Label labelPort;

    private Parent parent;
    private Stage stage;
    private Scene scene;

    private NetworkManager manager;

    private RPSGame game;
    private RPSState state;

    public MainController(Stage stage)
    {
        this.stage = stage;

        this.game = new RPSGame();
        this.state = new RPSState(this);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        fxmlLoader.setController(this);
        try
        {
            parent = fxmlLoader.load();
            scene = new Scene(parent, 600, 400);
        }
        catch (IOException e) {
            // manage the exception
        }
    }

    public void setNetworkManager(NetworkManager manager)
    {
        this.manager = manager;
        //serverId.setText(manager.getId());
    }

    public void displayScene(String username, String listeningPort)
    {
        this.labelUsername.setText(username);
        this.labelPort.setText("Listening on port: " + listeningPort);

        this.stage.setScene(this.scene);
        stage.show();
    }

    @Override
    public void onPeerConnected(Peer peer)
    {
        Platform.runLater(() -> {
            peersList.getItems().add(peer.getHost().getHostAddress() + ":" + peer.getPort());
            String line = "[PEER] Added peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n";
            logArea.appendText(line);
        });
    }

    @Override
    public void onPeerDisconnected(Peer peer)
    {
        Platform.runLater(() -> {
            logArea.appendText("[PEER] Disconnected peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n");
            peersList.getItems().remove(peer.getHost().getHostAddress() + ":" + peer.getPort());
        });
    }

    @Override
    public void onReceiveCommand(Peer peer, String data)
    {

        Platform.runLater(() -> {
            logArea.appendText("[CMD] Received command from peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n" +
                    "\tCommand: " + data + "\n");

            this.state.stateUpdate(RPSState.StateUpdate.ACTION_RECEIVED);
            if(peersList.getItems().size() == this.state.getNumberOfActionsReceived())
            {
                this.state.stateUpdate(RPSState.StateUpdate.ALL_ACTIONS_RECEIVED);
            }
        });
    }

    @Override
    public void onNotice(Peer peer, String msg)
    {
        Platform.runLater(() -> {
            logArea.appendText("[ERR] Network warning: " + msg + "\n");
        });
    }

    // Called when a action is clicked (rock/paper/scissors)
    public void action(ActionEvent actionEvent)
    {
        this.state.stateUpdate(RPSState.StateUpdate.ACTION_SENT);

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

    // Called when the peer connect button is clicked
    public void connectPeer(ActionEvent actionEvent)
    {
        int port = Integer.parseInt(peerPort.getText());
        String host = peerHost.getText();
        try {
            manager.addPeer(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(ActionEvent actionEvent)
    {
        // TODO
        System.out.println("Disconnect");
    }

    public void exit(ActionEvent actionEvent)
    {
        Platform.exit();
    }

    public void heartbeat(ActionEvent actionEvent)
    {
        RPSMessage message = new RPSMessage(RPSMessage.HEARTBEAT);
        manager.broadcast(message);
    }

    @Override
    public void onGameStateChanged(RPSState.State state)
    {
        Platform.runLater(() ->
        {
            switch (state)
            {
                case OPEN:
                    rockButton.setDisable(false);
                    paperButton.setDisable(false);
                    scissorsButton.setDisable(false);
                    connectButton.setDisable(false);

                    // TODO : Calculate scores and display result
                    logArea.appendText("GAME FINISHED - SHOW RESULT" + "\n");
                    break;

                case IN_PROGRESS:
                    connectButton.setDisable(true);
                    logArea.appendText("Game in progress.." + "\n");
                    break;

                case WAITING_FOR_PEERS:
                    rockButton.setDisable(true);
                    paperButton.setDisable(true);
                    scissorsButton.setDisable(true);
                    connectButton.setDisable(true);
                    logArea.appendText("Waiting for all peers to send their action.." + "\n");
                    break;

                case WAITING_FOR_SELF:
                    connectButton.setDisable(true);
                    logArea.appendText("Waiting for your action.." + "\n");
                    break;
            }
        });
    }
}
