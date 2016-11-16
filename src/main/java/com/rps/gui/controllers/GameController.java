package main.java.com.rps.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    public void initialize(URL location, ResourceBundle resources)
    {
        rockButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("ROCK pressed");
                actionSelected("rock");

                /*
                ProgressIndicator pi = new ProgressIndicator();
                VBox box = new VBox(pi);
                box.setAlignment(Pos.CENTER);
                // Grey Background
                //root.setDisable(true);
                //root.getChildren().add(box);
                */
            }
        });

        paperButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("PAPER pressed");
                actionSelected("paper");
            }
        });

        scissorsButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.out.println("SCISSORS pressed");
                actionSelected("scissors");
            }
        });

    }

    private void actionSelected(String action)
    {
        resultsLog.setText("You selected: " + action + ". Please wait for other peers");

        rockButton.setDisable(true);
        paperButton.setDisable(true);
        scissorsButton.setDisable(true);
    }
}
