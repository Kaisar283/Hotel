package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.Client;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ApartmentService {

    private final ApartmentStorage apartmentStorage;

    public ApartmentService(ApartmentStorage apartmentStorage) {
        this.apartmentStorage = apartmentStorage;
    }

    public List<Apartment> getAllApartments(){
        return apartmentStorage.getApartments();
    }

    public int registerApartment(double price){
        Apartment apartment = new Apartment(price);
        apartmentStorage.addApartment(apartment);
        return apartment.getId();
    }

    public ResultCode reserveApartment(int apartmentId, Client client){
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            return ResultCode.NOT_FOUND;
        } else if (apartmentOptional.get().isReserved()) {
            return ResultCode.RESERVED;
        } else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            apartment.setIsReserved(true);
            apartment.setReservedBy(client);
            return ResultCode.SUCCESS;
        }
    }

    public ResultCode releaseApartment(int apartmentId){
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            return ResultCode.NOT_FOUND;
        }else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            if(!apartment.isReserved()){
                return ResultCode.NOT_RESERVED;
            }else {
                apartment.setIsReserved(false);
                apartment.setReservedBy(null);
                return ResultCode.SUCCESS;
            }
        }
    }

    public List<Apartment> getApartmentsSortedByPrice(int page, int pageSize){
        List<Apartment> apartmentList = apartmentStorage.sortApartmentByPrice();
        return pagingApartments(page, pageSize, apartmentList);
    }

    public List<Apartment> getApartmentsSortedById(int page, int pageSize){
        List<Apartment> apartmentList = apartmentStorage.sortApartmentById();
        return pagingApartments(page, pageSize, apartmentList);
    }

    public List<Apartment> getApartmentSortedByReservationStatus(int page, int pageSize){
        List<Apartment> apartments = apartmentStorage.sortedApartmentByReservationStatus();
        return pagingApartments(page, pageSize, apartments);
    }

    public List<Apartment> getApartmentSortedByClientName(int page, int pageSize){
        List<Apartment> apartments = apartmentStorage.sortApartmentByClientName();
        return pagingApartments(page, pageSize, apartments);
    }

    public List<Apartment> pagingApartments(int page, int pageSize, @NotNull List<Apartment> apartments){
        int totalApartments = apartments.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalApartments);

        if(startIndex >= totalApartments || startIndex < 0){
            return List.of();
        }
        return apartments.subList(startIndex, endIndex);
    }


}
