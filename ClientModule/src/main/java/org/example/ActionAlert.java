package org.example;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ActionAlert {
    public static void showMessage(Stage owner, Alert.AlertType alertType, String title, String description){
        Alert message = new Alert(alertType);
        message.initOwner(owner);
        message.setTitle(title);
        message.setContentText(description);
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error Message");
        message.setContentText(text);
        message.showAndWait();
    }
//    ActionAlert.showErrorMessage(null, "Message can't be empty!");
}
