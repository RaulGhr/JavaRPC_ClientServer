package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Trip extends Entity<Long> implements Serializable {
    private String destinatie;
    private LocalDateTime dataOraPlecare;
    private int locuriDisponibile;

    public Trip(String destinatie, LocalDateTime dataOraPlecare, int locuriDisponibile) {
        this.destinatie = destinatie;
        this.dataOraPlecare = dataOraPlecare;
        this.locuriDisponibile = locuriDisponibile;
    }

    public String getDestinatie() {
        return destinatie;
    }

    public void setDestinatie(String destinatie) {
        this.destinatie = destinatie;
    }

    public LocalDateTime getDataOraPlecare() {
        return dataOraPlecare;
    }

    public void setDataOraPlecare(LocalDateTime dataOraPlecare) {
        this.dataOraPlecare = dataOraPlecare;
    }

    public int getLocuriDisponibile() {
        return locuriDisponibile;
    }

    public void setLocuriDisponibile(int locuriDisponibile) {
        this.locuriDisponibile = locuriDisponibile;
    }


}
