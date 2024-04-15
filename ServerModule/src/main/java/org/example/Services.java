package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Services implements IServices{

    private UserRepository userRepository;
    private TripRepository tripRepository;
    private ReservationRepository  reservationRepository;
    private Map<String, IObserver> loggedClients;
    private final int defaultThreadsNo=5;

    public Services(UserRepository userRepository, TripRepository tripRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public void addObserver(IObserver observer) throws AppException {

    }

    @Override
    public void addReservation(Long idCursa, String numeClient, int numarLoc) throws AppException {
        Trip trip = tripRepository.findOne(idCursa);

        if(trip.getLocuriDisponibile() - numarLoc < 0) {
            throw new AppException("Insufficient seats available!");
        }

        Reservation reservation = new Reservation(idCursa, numeClient, numarLoc, LocalDateTime.now());

        trip.setLocuriDisponibile(trip.getLocuriDisponibile() - numarLoc);
        tripRepository.update(trip);

        reservationRepository.save(reservation);
        notifyClients();
    }

    @Override
    public ArrayList<Reservation> getAllReservations() throws AppException {
        ArrayList<Reservation> reservations = new ArrayList<>();
        reservationRepository.findAll().forEach(reservations::add);
        return reservations;
    }

    @Override
    public ArrayList<Seat> getReservationsForTrip(Long idTrip) throws AppException {
        ArrayList<Reservation> reservations = this.getAllReservations();
        ArrayList<Reservation> reservationsForTrip = new ArrayList<>();
        for(Reservation reservation : reservations) {
            if(reservation.getIdCursa().equals(idTrip)) {
                reservationsForTrip.add(reservation);
            }
        }

        reservationsForTrip.stream()
                .sorted(Comparator.comparing(Reservation::getDataOraRezervare))
                .collect(Collectors.toList());

        ArrayList<Seat> seats = new ArrayList<>();
        int seatNumber = 1;
        for(Reservation reservation : reservationsForTrip) {
            for (int i = 0; i < reservation.getNumarLoc(); i++) {
                seats.add(new Seat(seatNumber, reservation.getNumeClient()));
                seatNumber++;
            }
        }
        return seats;
    }

    @Override
    public ArrayList<Trip> getAllTrips() throws AppException {
        ArrayList<Trip> trips = new ArrayList<>();
        tripRepository.findAll().forEach(trips::add);
        return trips;
    }

    @Override
    public Trip getTripByFilter(String destination, LocalDateTime date) throws AppException {
        ArrayList<Trip> trips = this.getAllTrips();

        for(Trip trip : trips){
            if(trip.getDestinatie().equals(destination) && trip.getDataOraPlecare().isEqual(date))
                return trip;
        }
        return null;
    }

    @Override
    public ArrayList<User> getAllUsers() throws AppException {
        ArrayList<User> users = new ArrayList<>();
        Iterable<User> all = userRepository.findAll();
        all.forEach(users::add);
//        for(User user : users)
//            System.out.println("user:" + user.getNumeUtilizator() + " " + user.getParola());
        return users;
    }

    @Override
    public User getUserById(Long id) throws AppException {
        return userRepository.findOne(id);
    }

    @Override
    public User logIn(String username, String password, IObserver client) throws AppException {
        for (User user : this.getAllUsers()) {
            if (user.getNumeUtilizator().equals(username) && user.getParola().equals(password)) {
                loggedClients.put(username, client);
                return user;
            }
        }
        throw new AppException("Invalid username or password!");
//        return null;
    }

    @Override
    public synchronized void logOut(String username) throws AppException{
        if (loggedClients.get(username) == null)
            throw new AppException("User not logged in");
        loggedClients.remove(username);
    }

    private void notifyClients(){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for(var client : loggedClients.values()){
            if(client == null)
                continue;
            executor.execute(client::reservationUpdate);
        }
    }
}
