package Reminder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

public class ReminderTaskManager {

    final static String JSON_FILE_NAME = "reminder.json";

    /**
     * Driver function.
     * @param args
     */
    public static void main(String[] args) {
//        System.out.println(ReminderTaskManager.markReminderComplete("07:06"));


    }


    /**
     * Returns reminders belonging to current day or null if, no reminders exists.
     *
     * @return HashMap with time as key and task as value
     */
    public static HashMap<String,HashMap<String,String>> getReminders() {

        JSONObject reminders = getRemindersForEveryDay();
        if (reminders != null) {
            String curDate = LocalDate.now().toString();
            JSONObject remindersForCurrentDay = (JSONObject) reminders.get(curDate);

            return remindersForCurrentDay;
        }
        else{
            return null;
        }
    }

    /**
     * Creates a new reminder with given time and task and adds it to JSON file.
     *
     * @param time time corresponding to task
     * @param message the work to be completed
     * @return HashMap consisting of all task's and their time.
     */
    public static HashMap<String, String> setNewReminder(String time, String message){

        JSONObject reminderMap = getRemindersForEveryDay();

        String curDate = LocalDate.now().toString();

        HashMap<String,HashMap<String,String>> reminderForDay = getReminders();

        if ( reminderForDay == null) {

            //Create new Task
            HashMap<String, HashMap<String,String>> task  = new HashMap<>();

            HashMap<String, String> taskDetails = new HashMap<>();

            taskDetails.put("time", time);
            taskDetails.put("message", message);
            taskDetails.put("status", "incomplete");

            task.put("task" + new Random().nextInt(1000000), taskDetails);

            //Add task to day
            reminderMap.put(curDate, task);
        }

        else {

            HashMap<String, String> taskDetails = new HashMap<>();
            taskDetails.put("time", time);
            taskDetails.put("message", message);
            taskDetails.put("status", "incomplete");
            reminderForDay.put("task" + new Random().nextInt(1000000), taskDetails);

            reminderMap.put(curDate, reminderForDay); //Replace previous tasks for new values + previous.
        }

            JSONObject jsonObject = new JSONObject(reminderMap);

            String jsonString = jsonObject.toJSONString();

        if (writeJsonToFile(jsonString)) {
            return reminderMap;
        }

        return null;

    }


    private static boolean writeJsonToFile(String jsonString){

        FileWriter writer = null;

        try {
            writer = new FileWriter(JSON_FILE_NAME);
            writer.write(jsonString);

            return true;

        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * Returns JSONObject representing all elements in JSON file.
     *
     * @return JSONObject of all elements.
     */
    private static JSONObject getRemindersForEveryDay() {

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(JSON_FILE_NAME));

            return jsonObject;


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Returns HashMap of shortest incomplete reminder, in time:(hh:mm:ss),message:(taskMsg) format or null if no reminders exists.
     *
     * @return HashMap time as key and task as value
     */
    public static HashMap<String,String> getShortestTimeReminder(){

        HashMap<String, HashMap<String, String>> reminders = getReminders();
        HashMap<String,String> reminderDetails = null;

        if(reminders == null){
            return null;
        }

        HashMap<String,String> shortestReminder = null;
        String st = "23:59:00";

        for (String i : reminders.keySet()) {
            //If the time is after the specified time

            HashMap<String, String> tempReminder = reminders.get(i);



            String time = tempReminder.get("time");
            String status = tempReminder.get("status");


            if(status.equals("incomplete")) {
                st = shortestTime(st, time);
                reminderDetails = tempReminder;

            }
        }

        if (reminderDetails != null) {
            shortestReminder = new HashMap<>();
            String time = reminderDetails.get("time");
            String message = reminderDetails.get("message");

            shortestReminder.put("time", time);
            shortestReminder.put("message", message);
        }


        return shortestReminder;
    }


    /**
     * Compares and returns shortest time string of the given strings, in case both are same returns s1.
     * @param s1 first time string in form hh:mm
     * @param s2 second time string in form hh:mm
     * @return Shorter time string of both.
     */
    private static String shortestTime(String s1,String s2){

        LocalTime t1 = LocalTime.parse(s1);
        LocalTime t2 = LocalTime.parse(s2);

        if (t1.isBefore(t2)) {
            return s1;
        }
        else if (t2.isBefore(t1)) {
            return s2;
        }
        else{
            return s1;
        }
    }

    /**
     * Marks all reminders with given time as complete
     * @param time time in hh:mm:ss.nm that the corresponding reminders to be marked as complete
     * @return JSONObject of modified JSON String.
     */

    public static JSONObject markReminderComplete(String time) {
        HashMap<String, HashMap<String, String>> reminders = getReminders();

        for (String key :reminders.keySet()) {
            HashMap<String, String> reminderDetails = reminders.get(key);
            String reminderStatus = reminderDetails.get("status");
            String reminderTime = reminderDetails.get("time");

            if (reminderStatus.equals("incomplete") && reminderTime.equals(time)) {
                reminderDetails.put("status", "complete");
                reminders.put(key, reminderDetails);
            }
        }

        JSONObject object = getRemindersForEveryDay(); //manage null exception
        object.put(LocalDate.now(),reminders);

        writeJsonToFile(object.toJSONString());

        return object;

    }




}
