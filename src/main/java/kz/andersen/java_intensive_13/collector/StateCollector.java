package kz.andersen.java_intensive_13.collector;

import com.fasterxml.jackson.annotation.JsonFormat;
import kz.andersen.java_intensive_13.enums.ApplicationOperations;
import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.models.Apartment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StateCollector {
    private List<Apartment> apartmentList;

    private ApplicationOperations executedOperation;

    private String inputCommand;

    private ResultCode resultCode = null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateTime;

    public List<Apartment> getApartmentList() {
        return apartmentList;
    }

    public void setApartmentList(List<Apartment> apartments) {
        this.apartmentList = apartments;
    }

    public ApplicationOperations getApplicationOperations() {
        return executedOperation;
    }

    public void setApplicationOperations(ApplicationOperations executedOperation) {
        this.executedOperation = executedOperation;
    }

    public String getInputCommand() {
        return inputCommand;
    }

    public void setInputCommand(String inputCommand) {
        this.inputCommand = inputCommand;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void addApartment(Apartment apartment){
        apartmentList = new ArrayList<>();
        apartmentList.add(apartment);
    }

    public static StateCollector collectState(List<Apartment> apartmentList,
                                       ApplicationOperations executedOperation,
                                       String inputCommand,
                                       ResultCode result){
        StateCollector stateCollector = new StateCollector();
        stateCollector.setApartmentList(apartmentList);
        stateCollector.setApplicationOperations(executedOperation);
        stateCollector.setInputCommand(inputCommand);
        stateCollector.setResultCode(result);
        stateCollector.setDateTime(LocalDateTime.now());
        return stateCollector;
    }

    public static StateCollector collectState(Apartment apartment,
                                       ApplicationOperations executedOperation,
                                       String inputCommand,
                                       ResultCode result){
        StateCollector stateCollector = new StateCollector();
        stateCollector.addApartment(apartment);
        stateCollector.setApplicationOperations(executedOperation);
        stateCollector.setInputCommand(inputCommand);
        stateCollector.setResultCode(result);
        stateCollector.setDateTime(LocalDateTime.now());
        return stateCollector;
    }

}
