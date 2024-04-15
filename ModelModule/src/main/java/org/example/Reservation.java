package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation extends Entity<Long> implements Serializable {
    private Long idCursa;
    private String numeClient;
    private int numarLoc;
    private LocalDateTime dataOraRezervare;

    public Reservation(Long idCursa, String numeClient, int numarLoc, LocalDateTime dataOraRezervare) {
        this.idCursa = idCursa;
        this.numeClient = numeClient;
        this.numarLoc = numarLoc;
        this.dataOraRezervare = dataOraRezervare;
    }

    public Long getIdCursa() {
        return idCursa;
    }

    public void setIdCursa(Long idCursa) {
        this.idCursa = idCursa;
    }

    public String getNumeClient() {
        return numeClient;
    }

    public void setNumeClient(String numeClient) {
        this.numeClient = numeClient;
    }

    public int getNumarLoc() {
        return numarLoc;
    }

    public void setNumarLoc(int numarLoc) {
        this.numarLoc = numarLoc;
    }

    public LocalDateTime getDataOraRezervare() {
        return dataOraRezervare;
    }

    public void setDataOraRezervare(LocalDateTime dataOraRezervare) {
        this.dataOraRezervare = dataOraRezervare;
    }


    public int compare(Reservation o1, Reservation o2) {
        // Access the date fields from your objects (modify as needed)
        LocalDateTime date1 = o1.getDataOraRezervare();
        LocalDateTime date2 = o2.getDataOraRezervare();

        // Compare dates using compareTo() for natural ordering
        return date1.compareTo(date2);
    }

}
