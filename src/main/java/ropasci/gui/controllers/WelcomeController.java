package ropasci.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ropasci.net.NetworkManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    private static final String WELCOME_SCREEN_FXML = "/welcome.fxml";
    @FXML
    public TextField tfUsername;
    @FXML
    public TextField tfListeningPort;
    @FXML
    private Button nextButton;

    private NetworkManager manager;

    private Parent parent;
    private Stage stage;
    private Scene scene;

    public WelcomeController(Stage stage) {
        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WELCOME_SCREEN_FXML));
        fxmlLoader.setController(this);
        try {
            parent = fxmlLoader.load();
            scene = new Scene(parent, 600, 400);
        } catch (IOException e) {
            // manage the exception
        }
    }

    public void displayScene() {
        this.stage.setScene(this.scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Hack to prevent autofocus on first field
        Platform.runLater(() -> tfUsername.getParent().requestFocus());

        // Context Menus for error messages
        final ContextMenu    usernameValidator = new ContextMenu();
        usernameValidator.setAutoHide(false);
        usernameValidator.getStyleClass().add("error");

        final ContextMenu listeningPortValidator = new ContextMenu();
        listeningPortValidator.setAutoHide(false);
        listeningPortValidator.getStyleClass().add("error");

        tfUsername.focusedProperty().addListener(
                (arg0, oldPropertyValue, newPropertyValue) -> {
                    if (newPropertyValue) {
                        // Clearing message if any
//                        actiontarget.setText("");
                        // Hiding the error message
                        usernameValidator.hide();
                    }
                });

        nextButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("Next pressed!");

                String username = tfUsername.getText();
                Integer listeningPort = null;

                //Validate username
                if (username == null || username.equals(""))
                {
                    usernameValidator.getItems().clear();
                    MenuItem it = new MenuItem("Please choose a username");
                    usernameValidator.getItems().add(it);
                    usernameValidator.show(tfUsername, Side.BOTTOM, 0, 10);

                    return;
                }

                // Validate listening port
                try
                {
                    listeningPort = Integer.valueOf(tfListeningPort.getText());
                }
                catch (NumberFormatException nfEx)
                {
                    System.out.println("invalid port");
                    listeningPortValidator.getItems().clear();
                    MenuItem it = new MenuItem("Invalid port");
                    listeningPortValidator.getItems().add(it);
                    listeningPortValidator.show(tfListeningPort, Side.BOTTOM, 10, 0);
                }

                try
                {
                    MainController mainController = new MainController(stage);
                    manager = new NetworkManager(listeningPort, mainController);
                    manager.setupServer();
                    manager.startNetworking();

                    mainController.setNetworkManager(manager);
                    mainController.displayScene(username, listeningPort.toString());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

        });
    }
}
