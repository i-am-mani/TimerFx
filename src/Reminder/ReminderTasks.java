package Reminder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReminderTasks {

    public String time, task;

    ReminderTasks(String time, String task) {

        this.time = LocalTime.parse(time).format(DateTimeFormatter.ofPattern("hh:mm"));
        this.task = task;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }


}
