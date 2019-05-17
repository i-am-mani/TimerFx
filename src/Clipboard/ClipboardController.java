package Clipboard;

import Main.ClipboardHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;


public class ClipboardController {


    @FXML
    ListView clipboardItems;

    ObservableList<Clip> clipObservableList;
    private Stage stage;

    public void initialize() {
        Platform.runLater(() -> {
            clipObservableList = getObservableList();

            clipboardItems.setItems(clipObservableList);
            clipboardItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            clipboardItems.setCellFactory(param -> new ListCell<Clip>() {

                @Override
                public void updateItem(Clip item, boolean empty) {
                    super.updateItem(item, empty);
                    String val = null;

                    if (item == null || empty) {

                    } else {
                        val = item.getValue();
                    }
                    setText(val);
                    setGraphic(null);

                }
            });
        });

    }

    private ObservableList<Clip> getObservableList() {
        ObservableList<Clip> observableList = FXCollections.observableArrayList();
        HashMap<String, HashMap<String, String>> clipboardHistory = ClipboardHandler.getClipboardHistory();

        for (String rowid : clipboardHistory.keySet()) {
            HashMap<String, String> hashMap = clipboardHistory.get(rowid);
            String datetime = hashMap.get("datetime");
            String value = hashMap.get("value");
            observableList.add(0, new Clip(rowid, datetime, value));
        }

        return observableList;
    }

    /**
     * All Selected List Cells will be copied to the System ClipBoard, it removes previous occurrences of the
     * Selected cells. The copied cells will be display at top of clipboard.
     *
     * @param e
     */
    public void onListItemClicked(MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            if (e.getClickCount() == 2) {
                StringBuilder copiedContent = new StringBuilder();
                ObservableList selectedItems = clipboardItems.getSelectionModel().getSelectedItems();
                for (Object selectedItem : selectedItems) {

                    Clip clip = (Clip) selectedItem;

                    //remove from existing clipboard
                    ClipboardHandler.deleteRow(clip.getRowid());

                    //copy to clipboard
                    String value = clip.getValue();
                    copiedContent.append(value);
                    copiedContent.append("\n");
                }
                ClipboardHandler.setClipboard(copiedContent.toString());
                refresh();
            }
        }
    }


    public void deleteSelectedClips() {
        ObservableList selectedItems = clipboardItems.getSelectionModel().getSelectedItems();
        ArrayList<String> arrayList = new ArrayList<>();
        for (Object selectedItem : selectedItems) {
            Clip clip = (Clip) selectedItem;
            String rowid = clip.getRowid();
            arrayList.add(rowid);
        }
        ClipboardHandler.deleteClips(arrayList);
        refresh();
    }

    public void deleteAllClips() {
        ClipboardHandler.deleteAllClips();
        refresh();
    }

    @FXML
    public void refreshObservableList(Event e) {
        refresh();
    }

    private void refresh() {
        clipObservableList.clear();
        clipObservableList.setAll(getObservableList());
    }

    @FXML
    public void closeApp(Event e){
        stage.close();
    }
    public void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

}


class Clip {
    public String date;
    public String value;
    public String rowid;


    Clip(String rowid,String date,String value){
        this.rowid = rowid;
        this.date = date;
        this.value = value;
    }

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

