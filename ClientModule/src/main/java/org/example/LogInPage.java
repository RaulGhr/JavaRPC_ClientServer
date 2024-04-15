package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;

public class LogInPage implements IObserver{
    public TextField usernameField;
    public TextField passwordField;

    private IServices server;


    public void setService(IServices server) {
        this.server = server;
    }

    public void handleLogIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = null;
        try {
            user = server.logIn(username, password,this);
        } catch (AppException e) {
            ActionAlert.showErrorMessage(null, e.getMessage());
        }


            try {

                FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("MainPage.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Main Page");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                dialogStage.setScene(scene);

                MainPage mainPageController = fxmlLoader.getController();

                mainPageController.setServer(server,user);

                Stage stageNow = (Stage) usernameField.getScene().getWindow();
                stageNow.hide();
                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void reservationUpdate() {

    }
}
