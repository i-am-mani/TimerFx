package Main;


import java.io.IOException;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class timerUI extends Application {
    double x,y;

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage primaryStage) throws IOException, ClassNotFoundException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("timerFXML.fxml"));
        Parent root= (Parent) loader.load();
        timerController controllerObj=(timerController) loader.getController();
        controllerObj.setStage(primaryStage);

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

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double max_x = bounds.getMaxX();
        double max_y = bounds.getMaxY();

        double mid_x = max_x/2;
        double mid_y = max_y/2;

        primaryStage.setX(mid_x-100);
        primaryStage.setY(0);
        primaryStage.getIcons().add(new Image("file:icons/clock.png"));
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}


