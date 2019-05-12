package Dialogs;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReminderDialogController {


    private String message;

    @FXML
    public TextArea messageArea;
    private Stage stage;


    public void closeApp(Event e) {
        stage.close();
    }

    public void setMessage(String task) {
        this.message = task;
        messageArea.setText(task);
    }

    public void setStage(Stage primaryStage) {
        stage = primaryStage;
    }
}
