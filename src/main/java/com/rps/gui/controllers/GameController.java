package main.java.com.rps.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    public Button rockButton;
    @FXML
    public Button paperButton;
    @FXML
    public Button scissorsButton;
    @FXML
    public TextArea resultsLog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
