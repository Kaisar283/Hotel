package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ApartmentService {

    private final ApartmentStorage apartmentStorage;

    public ApartmentService(){
        this.apartmentStorage = ApartmentStorage.getInstance();
    }

    public Optional<Apartment> getApartment(int apartmentId){
        return apartmentStorage.getApartmentById(apartmentId);
    }

    public List<Apartment> getAllApartments(){
        return apartmentStorage.getApartments();
    }

    public int registerApartment(Apartment apartment){
        apartmentStorage.addApartment(apartment);
        return apartment.getId();
    }

    public ResultCode reserveApartment(int apartmentId, User user) throws ResourceNotFoundException, AlreadyReservedException{
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Apartment with id " + apartmentId + " is not found!");
        } else if (apartmentOptional.get().getIsReserved()) {
            throw new AlreadyReservedException("Apartment with id " + apartmentId + " already reserved!");
        } else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            apartment.setIsReserved(true);
            apartment.setReservedBy(user);
            return ResultCode.SUCCESS;
        }
    }

    public ResultCode releaseApartment(int apartmentId){
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Apartment with id " + apartmentId + " is not found!");
        }else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            if(!apartment.getIsReserved()){
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
