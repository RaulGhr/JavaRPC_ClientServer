package org.example;

import java.io.Serializable;

public class Seat implements Serializable {
    private Integer seatNumber;
    private String name;

    public Seat(Integer seatNumber, String Name) {
        this.seatNumber = seatNumber;
        this.name = Name;
    }

    public Seat() {
        this.seatNumber = null;
        this.name = "-";
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
