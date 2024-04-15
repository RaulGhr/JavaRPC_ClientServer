package org.example;

import org.example.DTO.TripFilterDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRPCProxy implements IServices{
    private String host;
    private int port;

    public IObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private volatile boolean finished;
    private BlockingQueue<Response> queueResponses;

    public ServicesRPCProxy(String host, int port) {
        this.host = host;
        this.port = port;
        queueResponses = new LinkedBlockingQueue<>();
    }

    @Override
    public void addObserver(IObserver observer) throws AppException {
        this.client = observer;
    }

    @Override
    public void addReservation(Long idCursa, String numeClient, int numarLoc) throws AppException {

        Reservation reservation = new Reservation(idCursa, numeClient, numarLoc, LocalDateTime.now());
        Request request = new Request.Builder().type(RequestType.ADD_RESERVATION).data(reservation).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new AppException(error);
        }


    }

    @Override
    public ArrayList<Reservation> getAllReservations() throws AppException {
        return null;
    }

    @Override
    public ArrayList<Seat> getReservationsForTrip(Long idTrip) throws AppException {
        Request request = new Request.Builder().type(RequestType.GET_RESERVATIONS_FOR_TRIP).data(idTrip).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new AppException(error);
        }
        return (ArrayList<Seat>) response.getData();
    }

    @Override
    public ArrayList<Trip> getAllTrips() throws AppException {
        Request request = new Request.Builder().type(RequestType.GET_TRIPS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new AppException(error);
        }
        return (ArrayList<Trip>) response.getData();
    }

    @Override
    public Trip getTripByFilter(String destination, LocalDateTime date) throws AppException {
        Request request = new Request.Builder().type(RequestType.GET_TRIPS_FILTERED).data(new TripFilterDTO(destination, date)).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new AppException(error);
        }
        return (Trip) response.getData();

    }

    @Override
    public ArrayList<User> getAllUsers() throws AppException {
        return null;
    }

    @Override
    public User getUserById(Long id) throws AppException {
        return null;
    }

    @Override
    public User logIn(String username, String password, IObserver client) throws AppException {
        initializeConnection();
        User user = new User(username, password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.OK) {
            this.client = client;
            return (User) response.getData();
        }
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            closeConnection();
            throw new AppException(error);
        }
        return null;
    }

    @Override
    public void logOut(String username) throws AppException {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(username).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            String error = response.getData().toString();
            throw new AppException(error);
        }
        closeConnection();
    }

    private void initializeConnection() throws AppException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws AppException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new AppException("Error sending object " + e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = queueResponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        if (response.getType() == ResponseType.UPDATE) {
            client.reservationUpdate();
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object responseObj = input.readObject();
                    System.out.println("Response received " + responseObj);
                    if (responseObj instanceof Response) {
                        Response response = (Response) responseObj;
                        if (response.getType() == ResponseType.UPDATE) {
                            handleUpdate(response);
                        }
                        else {
                            try {
                                queueResponses.put(response);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
