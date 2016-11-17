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
import java.util.HashMap;

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

    private String username;
    private NetworkManager manager;

    private RPSGame game;
    private RPSState state;
    private HashMap<String, RPSGame.Action> playerActions;

    public MainController(Stage stage)
    {
        this.stage = stage;

        this.game = new RPSGame();
        this.state = new RPSState(this);
        this.playerActions = new HashMap<>();

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
    }

    public void displayScene(String username, String listeningPort)
    {
        this.username = username;
        this.game.addPlayer(this.username); // TODO
        this.labelUsername.setText(username);
        this.labelPort.setText("Listening on port: " + listeningPort);

        this.stage.setScene(this.scene);
        stage.show();
    }

    @Override
    public void onPeerConnected(Peer peer)
    {
        Platform.runLater(() -> {
            this.game.addPlayer(peer.getId());

            peersList.getItems().add(peer.getHost().getHostAddress() + ":" + peer.getPort());
            String line = "[PEER] Added peer at " + peer.getHost().getHostAddress() + ":" + peer.getPort() + "\n";
            logArea.appendText(line);
        });
    }

    @Override
    public void onPeerDisconnected(Peer peer)
    {
        Platform.runLater(() -> {

            if(this.playerActions.get(peer.getId()) != null)
            {
                this.playerActions.remove(peer.getId());
                this.state.stateUpdate(RPSState.StateUpdate.ACTION_REMOVED);
            }
            this.game.removePlayer(peer.getId());

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

            // TODO change this
            // RPSMessage.ACTION_ROCK ..

            if(data == "[1]")
            {
                //rock
                this.playerActions.put(peer.getId(), RPSGame.Action.ROCK);
            }
            else if(data == "[2]")
            {
                //paper
                this.playerActions.put(peer.getId(), RPSGame.Action.PAPER);
            }
            else if(data == "[3]")
            {
                //scissors
                this.playerActions.put(peer.getId(), RPSGame.Action.SCISSORS);
            }
            else
            {
                //TODO delete this else statement, this is just for temp testing
                this.playerActions.put(peer.getId(), RPSGame.Action.PAPER);
            }

            this.state.stateUpdate(RPSState.StateUpdate.ACTION_RECEIVED);
            if(peersList.getItems().size() == this.state.getNumberOfPeerActionsReceived())
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
        if (rockButton.equals(source))
        {
            this.playerActions.put(this.username, RPSGame.Action.ROCK);
            message = new RPSMessage(RPSMessage.ACTION, new byte[]{RPSMessage.ACTION_ROCK});
        }
        else if (paperButton.equals(source))
        {
            this.playerActions.put(this.username, RPSGame.Action.PAPER);
            message = new RPSMessage(RPSMessage.ACTION, new byte[]{RPSMessage.ACTION_PAPER});
        }
        else if (scissorsButton.equals(source))
        {
            this.playerActions.put(this.username, RPSGame.Action.SCISSORS);
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
                    displayScoreResults();
                    break;

                case IN_PROGRESS:
                    connectButton.setDisable(true);
                    logArea.appendText("Game in progress" + "\n");
                    break;

                case WAITING_FOR_PEERS:
                    rockButton.setDisable(true);
                    paperButton.setDisable(true);
                    scissorsButton.setDisable(true);
                    connectButton.setDisable(true);
                    logArea.appendText("Waiting for all peers to send their actions.." + "\n");
                    break;

                case WAITING_FOR_SELF:
                    connectButton.setDisable(true);
                    logArea.appendText("Waiting for your action.." + "\n");
                    break;
            }
        });
    }

    private void displayScoreResults()
    {
        logArea.appendText("Game finished - showing results" + "\n");
        game.calculateScore(playerActions);

        scoreArea.setText("Last game result:" + "\n");
        for (String id : this.game.getLastScores().keySet()) {
            scoreArea.appendText("id(" + id + "): " + this.game.getLastScores().get(id));
            scoreArea.appendText("\n");
        }

        game.updateGlobalScore();
        scoreArea.appendText("\n");
        scoreArea.appendText("Total score:" + "\n");
        for (String id : this.game.getGlobalScores().keySet()) {
            scoreArea.appendText("id(" + id + "): " + this.game.getTotalScoreForPlayer(id));
            scoreArea.appendText("\n");
        }

        this.playerActions.clear();
        logArea.appendText("New game open" + "\n");
    }
}
