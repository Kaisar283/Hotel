package services;

import models.Apartment;
import models.Client;
import repository.ApartmentStorage;

import java.util.*;

public class ApartmentService {

    private final ApartmentStorage apartmentStorage = new ApartmentStorage();

    public int registerApartment(double price){
        Apartment apartment = new Apartment(price);
        apartmentStorage.addApartment(apartment);
        return apartment.getId();
    }

    public void reserveApartment(int apartmentId, Client client){
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            System.out.println("Apartment with id " + apartmentId + " is not found");
        } else if (apartmentOptional.get().isReserved()) {
            System.out.println("Apartment already reserved.");
        } else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            apartment.setIsReserved(true);
            apartment.setReservedBy(client);
            System.out.println("Apartment successfully reserved by " + client.getName());
        }
    }

    public void releaseApartment(int apartmentId){
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            System.out.println("Apartment with id " + apartmentId + " not found");
        }else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            if(!apartment.isReserved()){
                System.out.println("This apartment with id " + apartment.getId() + " is not reserved.");
            }else {
                apartment.setIsReserved(false);
                apartment.setReservedBy(null);
                System.out.println("Apartment with id " + apartment.getId() + " released.");
            }
        }
    }

    public List<Apartment> getApartmentsSortedByPrice(int page, int pageSize){
        apartmentStorage.sortApartmentByPrice();
        return pagingApartments(page, pageSize);
    }

    public List<Apartment> getApartmentsSortedById(int page, int pageSize){
        apartmentStorage.sortApartmentById();
        return pagingApartments(page, pageSize);
    }

    public List<Apartment> getApartmentSortedByReservationStatus(int page, int pageSize){
        List<Apartment> apartments = apartmentStorage.sortedApartmentByReservationStatus();
        return pagingApartments(page, pageSize, apartments);
    }

    public List<Apartment> getApartmentSortedByClientName(int page, int pageSize){
        List<Apartment> apartments = apartmentStorage.sortApartmentByClientName();
        return pagingApartments(page, pageSize, apartments);
    }

    public List<Apartment> pagingApartments(int page, int pageSize){
        List<Apartment> apartments = apartmentStorage.getApartments();
        int totalApartments = apartments.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalApartments);

        if(startIndex >= totalApartments || startIndex < 0){
            return List.of();
        }
        return apartments.subList(startIndex, endIndex);
    }

    public List<Apartment> pagingApartments(int page, int pageSize, List<Apartment> apartments){
        int totalApartments = apartments.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalApartments);

        if(startIndex >= totalApartments || startIndex < 0){
            return List.of();
        }
        return apartments.subList(startIndex, endIndex);
    }


}
