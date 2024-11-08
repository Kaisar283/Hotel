package kz.andersen.java_intensive_13.models;

import java.util.Objects;

public class Apartment {
    private static int counter = 1;
    private int id;
    private double price;
    private boolean isReserved;
    private Client reservedBy;

    public Apartment(double price) {
        this.id = counter++;
        this.price = price;
        this.isReserved = false;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Apartment.counter = counter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setIsReserved(boolean reserved) {
        isReserved = reserved;
    }

    public Client getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(Client reservedBy) {
        this.reservedBy = reservedBy;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id = " + id +
                ", price = " + price +
                ", isReserved = " + isReserved +
                ", reservedBy = " + reservedBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return id == apartment.id && Double.compare(price, apartment.price) == 0 && isReserved == apartment.isReserved && Objects.equals(reservedBy, apartment.reservedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, isReserved, reservedBy);
    }
}
