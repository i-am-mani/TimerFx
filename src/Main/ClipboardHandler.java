package Main;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ClipboardHandler {

    private static Connection connection;
    private static Clipboard clipboard = Clipboard.getSystemClipboard();
    private String prevValue = "";

    private static void initConnection() {
        try {

            String url = "jdbc:sqlite:TimerFx.db";
            connection = DriverManager.getConnection(url);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setClipboard(String value) {
        HashMap<DataFormat, Object> hashMap = new HashMap<>();
        hashMap.put(DataFormat.PLAIN_TEXT, value);
        clipboard.setContent(hashMap);
    }

    private void createTable() {
        String query = "Create Table ClipboardHistory(date TEXT,time TEXT,value TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setNewClip() {
        String clip = getClip();
        initConnection();
        if (!isClipSameAsBefore(clip)) {
            System.out.println("New Clip " + clip);
            addClipToDb(clip);
        }
    }

    private void addClipToDb(String clip) {
        String date = LocalDateTime.now().toString();

        String insertStringQuery = "INSERT into ClipboardHistory values(?,?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertStringQuery);
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, clip);

            int count = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isClipSameAsBefore(String clip) {
        boolean r = clip.equals(prevValue);
        prevValue = clip;
        return r;
    }

    private String getClip() {
        String tempClip;
        tempClip = clipboard.getString();
        return tempClip;
    }

    public static HashMap<String, HashMap<String, String>> getClipboardHistory() {
        initConnection();
        HashMap<String, HashMap<String, String>> hashMap;
        try {
            String query = "Select rowid,* from ClipboardHistory Order by datetime(?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "datetime");

            ResultSet resultSet = preparedStatement.executeQuery();

            hashMap = getRowsHashMap(resultSet);
            return hashMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HashMap<String, HashMap<String,String>> getRowsHashMap(ResultSet resultSet){
        String datetime, value, rowid;
        HashMap<String,HashMap<String,String>> hashMap = new HashMap<>();
        try{
            while(resultSet.next()) {
                HashMap<String, String> rowHashMap = new HashMap<>();
                rowid = resultSet.getString("rowid");
                datetime = resultSet.getString("datetime");
                value = resultSet.getString("value");

                rowHashMap.put("datetime", datetime);
                rowHashMap.put("value", value);

                hashMap.put(rowid, rowHashMap);
            }
            System.out.println(hashMap);
            return hashMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void deleteRow(String rowid) {

        try{
            String query = "Delete from ClipboardHistory where rowid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,Integer.parseInt(rowid));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteClips(ArrayList<String> rowids) {
        for (String rowid : rowids) {
            deleteRow(rowid);
        }
    }

}
