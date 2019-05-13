package Reminder;

import com.jfoenix.controls.JFXTimePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Blend;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class reminderController {

    @FXML
    TableColumn timeColumn;

    @FXML
    TableColumn taskColumn;

    @FXML
    TableView tableView;

    @FXML
    TextField taskField;

    @FXML
    Label confirmationLabel;

    @FXML
    JFXTimePicker timePicker;

    ObservableList<ReminderTasks> tasksObservableList;
    private Stage stage;

    public void initialize() {

        tasksObservableList = getObservableList();

        timeColumn.setCellValueFactory(new PropertyValueFactory<ReminderTasks, String>("time"));
        taskColumn.setCellValueFactory(new PropertyValueFactory<ReminderTasks, String>("task"));

        tableView.setItems(tasksObservableList);
    }

    private ObservableList<ReminderTasks> getObservableList() {
        final ObservableList<ReminderTasks> reminders = FXCollections.observableArrayList();
        HashMap<String, HashMap<String, String>> remindersMap = ReminderTaskManager.getReminders();

        System.out.println(remindersMap);

        if (remindersMap != null) {
            for (String key : remindersMap.keySet()) {
                ReminderTasks reminderTasks = new ReminderTasks(remindersMap.get(key).get("time"), remindersMap.get(key).get("message"));
                reminders.add(reminderTasks);
            }
        }
        return reminders;
    }

    public void closeReminderManager(Event e){
        stage.close();
    }

    public void changeCloseTextColorToRed(Event e){
        Text text = (Text)e.getSource();
        text.setFill(Color.RED);
    }

    public void changeCloseTextColorToNormal(Event e){
        Text text = (Text) e.getSource();
        text.setFill(Color.GRAY);
    }

    public void setNewTask(Event e){
        String task = taskField.getText();
        String time = timePicker.getEditor().getText();
        time = convertTo24hourFormat(time);

        if (time.equals("") || task.equals("")) {
            confirmationLabel.setText("Something is not right..");
            return;
        }

        ReminderTaskManager.setNewReminder(time, task);
        refreshObservableList(time, task);
        setFieldsBlank();
    }

    private void setFieldsBlank() {
        taskField.clear();
        timePicker.getEditor().clear();
    }

    private String convertTo24hourFormat(String time) {


        String proper24HourFormat = LocalTime.parse(time, DateTimeFormatter.ofPattern("h:mm a")).toString();
        return proper24HourFormat;
    }

    public void refreshObservableList(String time, String task){
        ReminderTasks newTask = new ReminderTasks(time, task);
        tasksObservableList.add(newTask);
    }


    /**
     * Creates drop shadow effect of Node that calls the method.
     */
    public void dropShadowEffectOn(Event e) {

            Effect effect = new DropShadow(20, Color.web("#8db7dd"));

            ((Button) e.getSource()).setEffect(effect);

    }

    /**
     * Removes the drop shadow effect
     *
     * @param e
     */
    public void dropShadowEffectOff(Event e) {

        Effect effect = new Blend();
        ((Button) e.getSource()).setEffect(effect);
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
    }
}


