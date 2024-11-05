package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.models.Apartment;

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
        List<Apartment> apartmentList = new ArrayList<>();
        Collections.copy(apartmentList, apartments);
        apartmentList.sort(Comparator.comparingDouble(Apartment::getPrice));
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by Apartment ID, in ASC order.
     */
    public List<Apartment> sortApartmentById(){
        List<Apartment> apartmentList = new ArrayList<>();
        Collections.copy(apartmentList, apartments);
        apartmentList.sort(Comparator.comparingInt(Apartment::getId));
        return apartmentList;
    }

    /**
     * @return a new ArrayList sorted by Client name, in DESC order.
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
        apartments.sort(Comparator.comparing(Apartment::isReserved));
        return apartments.reversed();
    }

}
