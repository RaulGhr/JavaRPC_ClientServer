package org.example;

import org.example.DTO.TripFilterDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientRPCWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    public ClientRPCWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                connected = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (
                IOException e) {
            System.out.println("Error " + e);
        }
    }

    private void sendResponse(Response response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void reservationUpdate() {
        System.out.println("RESERVATION UPDATE");
        Response response = new Response.Builder().type(ResponseType.UPDATE).data(null).build();
        sendResponse(response);
    }

    private Response handleRequest(Request request){
        Response response = null;
        switch (request.getType()){
            case LOGIN:
                System.out.println("Login request ...");
                User userRequested = (User) request.getData();
                try {
                    User user = server.logIn(userRequested.getNumeUtilizator(), userRequested.getParola(),this);

                    return new Response.Builder().type(ResponseType.OK).data(user).build();

                } catch (Exception e) {
                    connected = false;
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }

            case LOGOUT:
                System.out.println("Logout request ...");
                String username = (String) request.getData();
                try {
                    server.logOut(username);
                    connected = false;
                    return okResponse;
                } catch (AppException e) {
                    connected = false;
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }

            case GET_TRIPS:
                System.out.println("Get trips request ...");
                try {
                    return new Response.Builder().type(ResponseType.OK).data(server.getAllTrips()).build();
                } catch (AppException e) {
                    connected = false;
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }

            case GET_TRIPS_FILTERED:
                System.out.println("Get trips filtered request ...");
                TripFilterDTO filter = (TripFilterDTO) request.getData();
                try {
                    return new Response.Builder().type(ResponseType.OK).data(server.getTripByFilter(filter.getDestination(),filter.getDate())).build();
                } catch (AppException e) {
                    connected = false;
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }

            case GET_RESERVATIONS_FOR_TRIP:
                System.out.println("Get reservations for trip request ...");
                Long idTrip = (Long) request.getData();
                try {
                    return new Response.Builder().type(ResponseType.OK).data(server.getReservationsForTrip(idTrip)).build();
                } catch (AppException e) {
                    connected = false;
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }

            case ADD_RESERVATION:
                System.out.println("Add reservation request ...");
                Reservation reservation = (Reservation) request.getData();
                try {
                    server.addReservation(reservation.getIdCursa(), reservation.getNumeClient(), reservation.getNumarLoc());
                    return okResponse;
                } catch (AppException e) {
                    connected = false;
                    return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
                }
        }
        return response;
    }
}
