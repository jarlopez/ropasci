package ropasci;

import javafx.application.Application;
import javafx.stage.Stage;
import ropasci.gui.controllers.WelcomeController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("P2P RPS");
        new WelcomeController(primaryStage).displayScene();
    }


    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

}


