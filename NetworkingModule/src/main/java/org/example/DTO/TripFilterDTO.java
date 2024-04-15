package org.example.DTO;

import org.example.DataFormatter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TripFilterDTO implements Serializable {
    String destination;
//    String date;
    LocalDateTime date;

//    DateTimeFormatter formatter = DataFormatter.getFormatter();

    public TripFilterDTO(String destination, LocalDateTime date) {
        this.destination = destination;
        this.date = date;

//        this.date = date.format(formatter);

    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDate() {
          return date;
//        return LocalDateTime.parse(date, formatter);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
//        this.date = date.format(formatter);
    }
}
