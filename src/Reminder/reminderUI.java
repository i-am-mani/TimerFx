package Reminder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class reminderUI extends Application {
    double x,y;

    public static void main(String[] args) {
        launch(args);
        System.out.println("Inside the ReminderUI");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("reminderFXML.fxml"));
        Parent root= (Parent) loader.load();

        reminderController reminderController = (reminderController) loader.getController();
        reminderController.setStage(primaryStage);

        Scene scene= new Scene(root);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);


        root.setOnMousePressed(event -> {
            x=event.getSceneX();
            y=event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX()-x);
            primaryStage.setY(event.getScreenY()-y);
        });


        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image("file:icons/reminder-icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
