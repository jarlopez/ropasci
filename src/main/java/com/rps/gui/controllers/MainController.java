package main.java.com.rps.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void createException(String msg) {
        System.out.println("Hereee");
        warningLabel.setText(msg);
    }
}
