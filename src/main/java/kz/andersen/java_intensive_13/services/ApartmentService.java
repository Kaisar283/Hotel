package kz.andersen.java_intensive_13.services;

import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import kz.andersen.java_intensive_13.repository.ApartmentStorage;
import kz.andersen.java_intensive_13.repository.UserRepository;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

public class ApartmentService {

    private ApartmentStorage apartmentStorage;
    private UserRepository userRepository;

    public ApartmentService() {
        this.apartmentStorage = new ApartmentStorage();
        this.userRepository = new UserRepository();
    }

    public ApartmentService(ApartmentStorage apartmentStorage,
                            UserRepository userRepository){
        this.apartmentStorage = apartmentStorage;
        this.userRepository = userRepository;
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
        Optional<User> userOptional = userRepository.findUserById(user.getId());
        if(apartmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Apartment with id " + apartmentId + " is not found!");
        } else if (apartmentOptional.get().isReserved()) {
            throw new AlreadyReservedException("Apartment with id " + apartmentId + " already reserved!");
        } else if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + user.getId() + " is not found!");
        } else {
            Apartment apartment = apartmentStorage.getApartmentById(apartmentId).get();
            apartment.setReserved(true);
            apartment.setUser(user);
            apartmentStorage.updateApartment(apartment);
            return ResultCode.SUCCESS;
        }
    }

    public ResultCode releaseApartment(int apartmentId){
        Optional<Apartment> apartmentOptional = apartmentStorage.getApartmentById(apartmentId);
        if(apartmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Apartment with id " + apartmentId + " is not found!");
        }else {
            Apartment apartment = apartmentOptional.get();
            if(!apartment.isReserved()){
                return ResultCode.NOT_RESERVED;
            }else {
                apartment.setReserved(false);
                apartment.setUser(null);
                apartmentStorage.updateApartment(apartment);
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
