package org.example;

import org.example.Utils.AbstractServer;
import org.example.Utils.RPCConcurrentServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRPCServer {
    private static int defaultPort = 55555;

    public static void main(String[] args)  {

        Properties props = new Properties();


        try {
            props.load(new FileReader("db.config"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find bd.config " + e);
        }

        Properties propertiesServer = new Properties();
        try {
            propertiesServer.load(new FileReader("server.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot find server.properties " + e);
        }


        UserRepository userRepository = new UserDBRepository(props);
        TripRepository tripRepository = new TripDBRepository(props);
        ReservationRepository reservationRepository = new ReservationDBRepository(props);
        IServices services = new Services(userRepository, tripRepository, reservationRepository);

        String port=propertiesServer.getProperty("Port");
        var intPort = Integer.parseInt(port);
        System.out.println("Starting server on port " + intPort);
        AbstractServer server = new RPCConcurrentServer(intPort, services);
        try{
            server.start();
        } catch (Exception e) {
            System.err.println("Error starting the server " + e.getMessage());
        }
    }
}
