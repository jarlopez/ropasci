package main.java.com.rps;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.rps.gui.controllers.MainController;
import main.java.com.rps.sandbox.SocketListener;

public class RPSApp extends Application {
    public static final String GUI_FXML_LOC = "/main/resources/rps-gui.fxml";
    private static final String MAIN_CSS_LOC = "/main/resources/css/rps.css";
    private static final String TITLE = "P2P RPS";
    private static final int HEIGHT = 400;
    private static final int WIDTH = 600;

    private SocketListener socketListener;

    private FXMLLoader loader;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
        try {

            loader = new FXMLLoader(getClass().getResource(GUI_FXML_LOC));
            Parent root = loader.load();

            root.getStylesheets().add(MAIN_CSS_LOC);
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
            primaryStage.show();

        } catch(Exception e) {
            showErrorDialog(Thread.currentThread(), e);
        }
    }

    private void showErrorDialog(Thread t, Throwable e) {
        System.out.println(e.toString());
        MainController controller = loader.getController();
        controller.createException("An uncaught exception was thrown in thread " + t
                + ". " + e.toString());
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        // TODO Perform cleanup
    }

    public static void main(String[] args) {
        launch(args);
    }

}
