package Main;

import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import util.Log;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ClipboardHandler {

    private final static Logger LOGGER = Log.getLogger(ClipboardHandler.class.getName());

    private static Connection connection;
    private static Clipboard clipboard = Clipboard.getSystemClipboard();
    private String prevValue = "";

    private static void initConnection() {
        try {
            if (connection == null) {
                String url = "jdbc:sqlite:TimerFx.db";
                connection = DriverManager.getConnection(url);
                createTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setClipboard(String value) {
        HashMap<DataFormat, Object> hashMap = new HashMap<>();
        hashMap.put(DataFormat.PLAIN_TEXT, value);
        clipboard.setContent(hashMap);

    }

    public static void setImageClipboard(Image imageClipboard){
        HashMap<DataFormat, Object> hashMap = new HashMap<>();
        hashMap.put(DataFormat.IMAGE,imageClipboard);
        clipboard.setContent(hashMap);
    }

    private static void createTable() {
        String query = "Create Table if not exists ClipboardHistory(datetime TEXT,value TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int i = preparedStatement.executeUpdate();
            LOGGER.info(i!=0?"New table Created" :" Existing table is used");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNewClip() {
        String clip = getClip();
        initConnection();
        if (!isClipSameAsBefore(clip)) {
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

            LOGGER.info("Added new clips " + clip);
            int count = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isClipSameAsBefore(String clip) {
        if(clip == null || clip.length() < 0){
            return true;
        }
        else{
            boolean r = clip.equals(prevValue);
            prevValue = clip;
            return r;

        }
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
            return hashMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteAllClips() {
        try {
            String query = "DELETE from ClipboardHistory";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int rowAffected = preparedStatement.executeUpdate();
            LOGGER.info("Rows Affected = " + String.valueOf(rowAffected));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteRow(String rowid) {

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
