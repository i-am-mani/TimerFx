package Dialogs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ReminderDialogUI extends Application{
    private static  String REMINDER_MESSAGE = "";
    double x,y;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        Application.launch(args,"Nothing");
        initDialog("Nothing new");
    }

    public static void initDialog(String task){
        System.out.println("Starting Dialog");
        REMINDER_MESSAGE = task;
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    new ReminderDialogUI().start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start(Stage primaryStage) throws   IOException, ClassNotFoundException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("reminderDialogFXML.fxml"));
        Parent root= (Parent) loader.load();

        ReminderDialogController controller = (ReminderDialogController) loader.getController();
        controller.setMessage(REMINDER_MESSAGE);
        System.out.println(REMINDER_MESSAGE);
        controller.setStage(primaryStage);


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



        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
