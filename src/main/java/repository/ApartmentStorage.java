package repository;

import models.Apartment;

import java.util.*;

public class ApartmentStorage {

    private static List<Apartment> apartments = new ArrayList<>();

    static {
        apartments.add(new Apartment(1000));
        apartments.add(new Apartment(2000));
        apartments.add(new Apartment(1300));
        apartments.add(new Apartment(1500));
        apartments.add(new Apartment(1900));
        apartments.add(new Apartment(2200));
        apartments.add(new Apartment(4400));
        apartments.add(new Apartment(4000));
        apartments.add(new Apartment(5000));
        apartments.add(new Apartment(3500));
    }

    public void addApartment(Apartment apartment){
        apartments.add(apartment);
    }

    public List<Apartment> getApartments(){
        return apartments;
    }


    public Optional<Apartment> getApartmentById(int apartmentId){
        for (Apartment apartment:apartments) {
            if(apartment.getId() == apartmentId){
                return Optional.of(apartment);
            }
        }
        return Optional.empty();
    }

    public void sortApartmentByPrice(){
        apartments.sort(Comparator.comparingDouble(Apartment::getPrice));
    }

    public void sortApartmentById(){
        apartments.sort(Comparator.comparingInt(Apartment::getId));
    }

    public List<Apartment> sortApartmentByClientName() {
        apartments.sort(Comparator.comparing(
                apartment -> apartment.getReservedBy() == null ? "" : apartment.getReservedBy().getName()
        ));
        return apartments.reversed();
    }

    public List<Apartment> sortedApartmentByReservationStatus(){
        apartments.sort(Comparator.comparing(Apartment::isReserved));
        return apartments.reversed();
    }

}
