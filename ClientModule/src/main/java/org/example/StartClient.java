package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import java.io.IOException;

public class StartClient extends Application{


    public static void main(String[] args) {
        launch(args);
//        System.out.println("Hello");
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("LogInPage.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        LogInPage logInPageControler = fxmlLoader.getController();

        Properties propertiesClient = new Properties();

        try {
            propertiesClient.load(new FileReader("client.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find client.properties " + e);
        }
        var port = Integer.parseInt(propertiesClient.getProperty("port"));
        var host = propertiesClient.getProperty("host");
        IServices server = new ServicesRPCProxy(host, port);

        logInPageControler.setService(server);
        primaryStage.setTitle("Transport Company");
        primaryStage.show();

    }


}
