package main.java.com.rps.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.java.com.rps.RPSApp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public AnchorPane peersView;
    @FXML
    public AnchorPane resultsView;
    @FXML
    public AnchorPane gameView;
    @FXML
    public Text warningLabel;

    private Parent parent;
    private Stage stage;
    private Scene scene;

    public MainController(Stage stage)
    {
        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RPSApp.GUI_FXML_LOC));
        fxmlLoader.setController(this);

        try
        {
            parent = fxmlLoader.load();
            parent.getStylesheets().add(RPSApp.MAIN_CSS_LOC);
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
    }

    @FXML
    public void createException(String msg) {
        System.out.println("Hereee");
        warningLabel.setText(msg);
    }
}
