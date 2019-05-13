package Clipboard;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClipboardUI extends Application {
    double x,y;
    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("clipboardFXML.fxml"));
        Parent root= (Parent) loader.load();

        ClipboardController clipboardController = loader.getController();
        clipboardController.setStage(primaryStage);

        Scene scene= new Scene(root);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        root.setOnMousePressed(new EventHandler<MouseEvent>(){

            public void handle(MouseEvent event) {
                x=event.getSceneX();
                y=event.getSceneY();
            }
        });


        root.setOnMouseDragged(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX()-x);
                primaryStage.setY(event.getScreenY()-y);
            }
        });


        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image("file:icons/scissors.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
