package main.java.com.rps.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.com.rps.RPSApp;
import main.java.com.rps.utils.ValidationUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseGameController implements Initializable
{
    @FXML
    private Label labelChooseGame;
    @FXML
    private Button startNewGameButton;
    @FXML
    public TextField tfHostAddress;
    @FXML
    public TextField tfHostPort;
    @FXML
    private Button joinGameButton;

    //TODO : get from peer class?
    private String username;

    private Parent parent;
    private Stage stage;
    private Scene scene;

    public ChooseGameController(Stage stage, String username)
    {
        this.stage = stage;
        this.username = username;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/resources/choose-game-scene.fxml"));
        fxmlLoader.setController(this);
        try
        {
            parent = fxmlLoader.load();
            scene = new Scene(parent, RPSApp.WIDTH, RPSApp.HEIGHT);
        }
        catch (IOException e)
        {
            // manage the exception
        }
    }

    public void displayScene()
    {
        this.stage.setScene(this.scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Hack to prevent autofocus on first field
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tfHostAddress.getParent().requestFocus();
            }
        });

        this.labelChooseGame.setText("Hi " + username + "!");

        // Context Menus for error messages
        final ContextMenu addressValidator = new ContextMenu();
        addressValidator.setAutoHide(false);
        addressValidator.getStyleClass().add("Error");

        final ContextMenu portValidator = new ContextMenu();
        portValidator.setAutoHide(false);
        portValidator.getStyleClass().add("Error");

        startNewGameButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                new MainController(stage).displayScene();
            }
        });

        joinGameButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("Join game pressed!");

                String hostAddress = tfHostAddress.getText();
                Integer hostPort = null;

                // Validate host address
                if (!ValidationUtil.validateNotEmpty(hostAddress))
                {
                    addressValidator.getItems().clear();
                    MenuItem it = new MenuItem("Invalid host address");
                    addressValidator.getItems().add(it);
                    addressValidator.show(tfHostAddress, Side.BOTTOM, 0, 10);
                }

                // Validate host port
                try
                {
                    hostPort = Integer.valueOf(tfHostPort.getText());
                }
                catch (NumberFormatException nfEx)
                {
                    addressValidator.getItems().clear();
                    MenuItem it = new MenuItem("Invalid host port");
                    addressValidator.getItems().add(it);
                    addressValidator.show(tfHostPort, Side.BOTTOM, 0, 10);
                }
            }

            // TODO : Initiate the Peer class and connect to the peer
            // TODO : on success, display the main controller

        });


    }
}
