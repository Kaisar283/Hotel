package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.services.ApartmentService;

import java.util.*;

public class ApartmentStorage {

    private static volatile ApartmentStorage instance;

    public static ApartmentStorage getInstance(){
        if(instance == null){
            synchronized (ApartmentStorage.class){
                if (instance == null){
                    instance =  new ApartmentStorage();
                }
            }
        }
        return instance;
    }

    private final List<Apartment> apartments = new ArrayList<>();

    /**
     * Method: add apartment to original list.
     * @param apartment - an Apartment instance
     */
    public void addApartment(Apartment apartment){
        apartments.add(apartment);
    }

    /**
     * @return the original Apartments list
     */
    public List<Apartment> getApartments(){
        return apartments;
    }


    /**
     * Return Optional<Apartment> if given ID it exists in the Apartment Storage,
     * else return an empty Optional
     * @param apartmentId apartment ID
     * @return an Apartment Optional by given ID
     */
    public Optional<Apartment> getApartmentById(int apartmentId){
        for (Apartment apartment:apartments) {
            if(apartment.getId() == apartmentId){
                return Optional.of(apartment);
            }
        }
        return Optional.empty();
    }

    /**
     * @return a new ArrayList sorted by Apartment price, in ASC order.
     */
    public List<Apartment> sortApartmentByPrice(){
        List<Apartment> apartmentList = new ArrayList<>(Collections.unmodifiableList(apartments));
        apartmentList.sort(Comparator.comparingDouble(Apartment::getPrice));
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by Apartment ID, in ASC order.
     */
    public List<Apartment> sortApartmentById(){
        List<Apartment> apartmentList = new ArrayList<>(Collections.unmodifiableList(apartments));
        apartmentList.sort(Comparator.comparingInt(Apartment::getId));
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by User name, in DESC order.
     */
    public List<Apartment> sortApartmentByClientName() {
        apartments.sort(Comparator.comparing(
                apartment -> apartment.getReservedBy() == null ? "" : apartment.getReservedBy().getName()
        ));
        return apartments.reversed();
    }

    /**
     * @return a new ArrayList sorted by Reservation status, in DESC order.
     */
    public List<Apartment> sortedApartmentByReservationStatus(){
        apartments.sort(Comparator.comparing(Apartment::getIsReserved));
        return apartments.reversed();
    }

}
