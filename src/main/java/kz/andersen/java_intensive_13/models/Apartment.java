package kz.andersen.java_intensive_13.models;

public class Apartment {
    private static int counter = 1;
    private final int id;
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
}
