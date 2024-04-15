package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IServices {

    void addObserver(IObserver observer) throws AppException;

    // RESERVATION FUNCIONS
    void addReservation(Long idCursa, String numeClient, int numarLoc) throws AppException;
    ArrayList<Reservation> getAllReservations() throws AppException;
    ArrayList<Seat> getReservationsForTrip(Long idTrip) throws AppException;

    // TRIP FUNCTIONS
    ArrayList<Trip> getAllTrips() throws AppException;
    Trip getTripByFilter(String destination, LocalDateTime date) throws AppException;

    //USER FUNCTIONS
    ArrayList<User> getAllUsers() throws AppException;
    User getUserById(Long id) throws AppException;
    User logIn(String username, String password, IObserver client) throws AppException;
    void logOut(String username) throws AppException;



}
