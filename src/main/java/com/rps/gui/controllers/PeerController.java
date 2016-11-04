package main.java.com.rps.gui.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import main.java.com.rps.sandbox.ConnectionStatus;
import main.java.com.rps.sandbox.PeerConnection;
import main.java.com.rps.sandbox.SocketListener;
import main.java.com.rps.utils.ValidationUtil;

import java.io.IOException;
import java.net.BindException;
import java.net.URL;
import java.util.ResourceBundle;

import static main.java.com.rps.utils.PeerUtil.MAX_PEERS;

public class PeerController implements Initializable {
    @FXML
    public TextField peerIpAddress;
    @FXML
    public TextField peerPort;
    @FXML
    public Button addPeerButton;
    @FXML
    public ListView existingPeersList;
    @FXML
    public Button exitButton;
    @FXML
    public Button disconnectButton;
    @FXML
    public TextField serverPort;
    @FXML
    public Button startServer;
    @FXML
    public Circle serverStatusColor;

    private SocketListener socketListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Hack to prevent autofocus on first field
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                peerIpAddress.getParent().requestFocus();
            }
        });

        // Context Menus for error messages
        final ContextMenu ipValidator = new ContextMenu();
        ipValidator.setAutoHide(false);
        ipValidator.getStyleClass().add("error");

        final ContextMenu portValidator = new ContextMenu();
        portValidator.setAutoHide(false);
        portValidator.getStyleClass().add("error");

        final ContextMenu peerNumValidator = new ContextMenu();
        peerNumValidator.setAutoHide(false);
        peerNumValidator.getStyleClass().add("error");

        final ContextMenu serverValidator = new ContextMenu();
        serverValidator.setAutoHide(false);
        serverValidator.getStyleClass().add("error");

        // Manage event handlers
        peerIpAddress.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(
                    ObservableValue<? extends Boolean> arg0,
                    Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    peerIpAddress.setText("");
                    ipValidator.hide();
                }
            }
        });
        peerPort.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(
                    ObservableValue<? extends Boolean> arg0,
                    Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    peerPort.setText("");
                    portValidator.hide();
                }
            }
        });

        addPeerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (existingPeersList.getItems().size() >= MAX_PEERS) {
                    peerNumValidator.getItems().clear();
                    MenuItem it = new MenuItem("Already at max peers");
                    peerNumValidator.getItems().add(it);
                    peerNumValidator.show(addPeerButton, Side.RIGHT, 5, 0);
                    return;
                }
                String ip = peerIpAddress.getText();
                Integer port = null;
                // Validate port & IP
                try {
                    port = Integer.valueOf(peerPort.getText());
                } catch (NumberFormatException nfEx) {
                    portValidator.getItems().clear();
                    MenuItem it = new MenuItem("Invalid port");
                    portValidator.getItems().add(it);
                    portValidator.show(peerPort, Side.BOTTOM, 10, 0);
                }

                if (!ValidationUtil.validateNotEmpty(ip)) {
                    ipValidator.getItems().clear();
                    MenuItem it = new MenuItem("Invalid IP address");
                    ipValidator.getItems().add(it);
                    ipValidator.show(peerIpAddress, Side.BOTTOM, 0, 0);
                }
                if (ip == null || port == null) {
                    return;
                }
                // TODO Add peer (with status={CONNECTING, ACTIVE, INACTIVE} to list of peers)
                existingPeersList.getItems().add(ip + ":" + port);
                // TODO Dispatch conn. attempt to core utils
            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Trigger peer cleanup, terminate safely, etc.
                Platform.exit();
            }
        });

        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Peer disconnect & cleanup (send 'QUIT' command to alert peers)
            }
        });


        PeerController parent = this;
        startServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    socketListener = new SocketListener(Integer.valueOf(serverPort.getText()));
                    socketListener.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                        // update controller on FX Application Thread:
                        ConnectionStatus status = ConnectionStatus.valueOf(newStatus);
                        switch (status) {
                            case ONLINE:
                                break;
                            case OFFLINE:
                                break;
                            case ERROR:
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        serverValidator.getItems().clear();
                                        MenuItem it = new MenuItem("Unable to create listener");
                                        serverValidator.getItems().add(it);
                                        serverValidator.show(startServer, Side.BOTTOM, 5, 0);
                                    }
                                });break;
                            case AWAITING_CONNECTION:
                                break;
                            case EXITED:
                                break;
                            default:
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (status != ConnectionStatus.ERROR) {
                                    serverValidator.hide();
                                }
                                serverStatusColor.setFill(status.getColor());
                            }
                        });
                    });
                    socketListener.peerDataProperty().addListener((obs, oldList, newList) -> {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Pull out into generic generator
                                PeerConnection conn = newList.get(0);
                                HBox container = new HBox();
                                container.setAlignment(Pos.CENTER_LEFT);
                                Label displayText = new Label(conn.getHost() + ":" + conn.getPort());
                                Circle statusCircle = new Circle(4, ConnectionStatus.ONLINE.getColor());
                                statusCircle.setStroke(Color.BLACK);
                                statusCircle.setStrokeType(StrokeType.INSIDE);
                                container.setSpacing(10);
                                container.getChildren().add(statusCircle);
                                container.getChildren().add(displayText);
                                existingPeersList.getItems().add(container);
                            }
                        });
                    });
                } catch (BindException bEx) {
                    ipValidator.getItems().clear();
                    MenuItem it = new MenuItem("Could not listen on the given port");
                    ipValidator.getItems().add(it);
                    ipValidator.show(startServer, Side.RIGHT, 0, 0);
                } catch (IOException e) {
                    ipValidator.getItems().clear();
                    MenuItem it = new MenuItem("I/O Exception when starting listener");
                    ipValidator.getItems().add(it);
                    ipValidator.show(startServer, Side.RIGHT, 0, 0);
                }
            }
        });

    }

    private void addConnection(ObservableList<PeerConnection> conns) {
        existingPeersList.getItems().add(conns.get(0).toString());

    }
}
