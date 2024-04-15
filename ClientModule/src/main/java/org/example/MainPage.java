package org.example;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainPage implements IObserver{
    public TableView allTripsTable;
    public TableColumn allTripsTableDestinationField;
    public TableColumn allTripsTableLeaveTimeField;
    public TableColumn allTripsTableSeatsField;
    public TableView searchedTripTable;
    public TableColumn searchedTripTableSeatNumberField;
    public TableColumn searchedTripTableNameFiled;
    public TextField destinationField;
    public DatePicker dataPickerElement;
    public TextField leaveTimeFiled;
    public TextField nameFiled;
    public TextField seatsNrField;

    private IServices server;

    Long currentTripId;

    User currentUser;

    ObservableList<Trip> tripObsList = FXCollections.observableArrayList();
    ObservableList<Seat> seatsObsList = FXCollections.observableArrayList();




    @FXML
    public void initialize() {
        allTripsTableDestinationField.setCellValueFactory(new PropertyValueFactory<Trip, String>("destinatie"));
        allTripsTableLeaveTimeField.setCellValueFactory(new PropertyValueFactory<Trip, String>("dataOraPlecare"));
        allTripsTableSeatsField.setCellValueFactory(new PropertyValueFactory<Trip, Integer>("locuriDisponibile"));
        allTripsTable.setItems(tripObsList);

        searchedTripTableSeatNumberField.setCellValueFactory(new PropertyValueFactory<Trip, Integer>("seatNumber"));
        searchedTripTableNameFiled.setCellValueFactory(new PropertyValueFactory<Trip, String>("name"));
        searchedTripTable.setItems(seatsObsList);

        allTripsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Trip trip = (Trip) newValue;
                currentTripId = trip.getId();
            }
        });

    }

    public void initializeTableTrips() {
        try {
            tripObsList.setAll(server.getAllTrips());
        } catch (AppException e) {
            ActionAlert.showErrorMessage(null, e.getMessage());
        }

    }

    public void setServer(IServices server, User user) {
        this.server = server;
        this.currentUser = user;

        try {
            server.addObserver(this);
        } catch (AppException e) {
            ActionAlert.showErrorMessage(null, e.getMessage());
        }


        this.initializeTableTrips();

    }

    public void handleSearch() {
        if (destinationField.getText().isEmpty() || dataPickerElement.getValue() == null || leaveTimeFiled.getText().isEmpty()) {
            return;
        }

        String destination = destinationField.getText();
        String leaveDate = dataPickerElement.getValue().toString();
        String leaveHour = leaveTimeFiled.getText();



        LocalDateTime leaveTime = LocalDateTime.parse(leaveDate + "T" + leaveHour, DataFormatter.getFormatter());

        try {
            Trip trip = server.getTripByFilter(destination, leaveTime);
            if (trip != null) {
                this.currentTripId = trip.getId();
                ArrayList<Seat> seats = server.getReservationsForTrip(trip.getId());
                seatsObsList.setAll(seats);
            }
        } catch (AppException e) {
            ActionAlert.showErrorMessage(null, e.getMessage());
        }

    }

    public void handleReserve() {
        String name = nameFiled.getText();
        Integer seatsNr = Integer.parseInt(seatsNrField.getText());
        try {
            server.addReservation(currentTripId, name, seatsNr);
        } catch (AppException e) {
            ActionAlert.showErrorMessage(null, e.getMessage());
        }


    }

    public void handleLogout() {
        try {
            server.logOut(currentUser.getNumeUtilizator());
            Stage stage = (Stage) allTripsTable.getScene().getWindow();
            stage.close();
        } catch (AppException e) {
            ActionAlert.showErrorMessage(null, e.getMessage());
        }
    }


    @Override
    public void reservationUpdate() {
        Platform.runLater(() -> {
            System.out.println("Reservation updated");
            this.initializeTableTrips();
            this.handleSearch();
        });
    }
}
