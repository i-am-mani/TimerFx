package Main;

import Clipboard.ClipboardUI;
import Dialogs.ReminderDialogUI;
import Reminder.ReminderTaskManager;
import Screenshot.ScreenshotApp;
import animatefx.animation.Shake;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class timerController {

    @FXML
    Label hourLabel;
    @FXML
    Label minuteLabel;

    @FXML
    AnchorPane mainLayout;
    private Stage stage;

    @FXML
    AnchorPane reminderApp;
    @FXML
    AnchorPane screenshotApp;
    @FXML
    AnchorPane clipboardApp;

    @FXML
    AnchorPane timeLayout;


    private Reminder.reminderUI reminder;

    private ClipboardUI clipboardUI;

    private ScreenshotApp screenshot;


    @FXML
    public void initialize() {

        TimeHandler timeHandler = new TimeHandler(hourLabel, minuteLabel, timeLayout);
        ClipboardHandler clipboardHandler = new ClipboardHandler();


        Timeline clock = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            timeHandler.handleTime();
        }),new KeyFrame(Duration.seconds(1),e->{
            clipboardHandler.setNewClip();
        }));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }

    @FXML
    public void fadeOutTimer(Event e) {
        reminderApp.setVisible(false);
        screenshotApp.setVisible(false);
        clipboardApp.setVisible(false);
        mainLayout.setStyle("-fx-background-color:transparent");

    }

    @FXML
    public void fadeInTimer(Event e) {
        reminderApp.setVisible(true);
        screenshotApp.setVisible(true);
        clipboardApp.setVisible(true);
        mainLayout.setStyle(" -fx-background : rgba(38, 50, 56,0.1)");
    }

    @FXML
    public void initClipboardApp(Event e){
        if (clipboardUI != null) {
            clipboardUI.closeApp();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    clipboardUI = new ClipboardUI();
                    clipboardUI.start(new Stage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }



    @FXML
    public void initReminderApp(Event e){
        if (reminder != null) {
            reminder.closeApp();
        }

        Platform.runLater(() -> {
            try {
                reminder = new Reminder.reminderUI();
                reminder.start(new Stage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

    }

    @FXML
    public void initScreenshot(Event event) {
        if (screenshot != null) {
            screenshot.toFront();
        } else {
            Platform.runLater(() -> {
                try {
                    screenshot = new ScreenshotApp();
                    screenshot.start(new Stage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        }

    }


    @FXML
    public void close(Event e){
        stage.close();
    }


    public void setStage(Stage primaryStage) {
        stage = primaryStage;
    }
}



class TimeHandler{

    private boolean HIGHLIGHTED = false;
    private final Label minuteLabel;
    private final Label hourLabel;
    private final AnchorPane layout;
    private boolean isHourFormat12 = true;
    private LocalTime LOCAL_TIME;
    private LocalTime _prevTaskTime ;

    TimeHandler(Label hour, Label minute, AnchorPane layout){
        this.hourLabel = hour;
        this.minuteLabel = minute;
        this.layout = layout;
    }

    public void handleTime(){
        LOCAL_TIME = LocalTime.now();
        String curTime;

        if(isHourFormat12 == true){
            curTime = get12HourFormat();
        }
        else {
            curTime = LOCAL_TIME.toString();
        }

        updateTime(curTime);
        hourlyHighlight();
        checkReminders();
    }

    private void hourlyHighlight(){

        int hour = LOCAL_TIME.getHour();
        int minute = LOCAL_TIME.getMinute();
        int seconds = LOCAL_TIME.getSecond();
        if(minute == 0 && seconds == 0){
            new Shake(layout).play();
        }

    }

    private void checkReminders() {

        HashMap<String, String> reminder = ReminderTaskManager.getShortestTimeReminder();

        if( reminder == null){
            return;
        }

        String time = reminder.get("time");
        String message = reminder.get("message");

        LocalTime shortestReminderTime = LocalTime.parse(time);


        int h = shortestReminderTime.getHour(), m = shortestReminderTime.getMinute();
        int curHour = LOCAL_TIME.getHour(), curMinute = LOCAL_TIME.getMinute();

        if (h == curHour && m == curMinute) {
            showMessage(message);
            ReminderTaskManager.markReminderComplete(time);
        }
        else if (LOCAL_TIME.isAfter(shortestReminderTime)) {
            ReminderTaskManager.markReminderComplete(time);
            System.out.println("REMINDER_CheckReminder");
        }
    }

    private void showMessage(String message) {
            ReminderDialogUI.initDialog(message);
    }


    private void updateTime(String curTime) {
        String hour = curTime.substring(0,2);
        String minute = curTime.substring(3,5);

        hourLabel.setText(hour);
        minuteLabel.setText(minute);
    }

    private String get12HourFormat(){
        DateTimeFormatter hourFormat12 = DateTimeFormatter.ofPattern("hh:mm:ss");
        LocalTime localTime = LocalTime.now();
        String time = localTime.format(hourFormat12);
        return time;
    }

}


