package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.handler.ExceptionHandler;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.User;
import kz.andersen.java_intensive_13.services.ApartmentService;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/apartment/reserve")
public class ReserveApartmentServlet extends HttpServlet {
    ApartmentService apartmentService;
    ObjectMapper objectMapper;
    ExceptionHandler exceptionHandler;

    public ReserveApartmentServlet(ApartmentService apartmentService,
                                   ObjectMapper objectMapper,
                                   ExceptionHandler exceptionHandler){
        this.apartmentService = apartmentService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    public ReserveApartmentServlet() {
        this.apartmentService = new ApartmentService();
        this.objectMapper = new ObjectMapper();
        this.exceptionHandler = new ExceptionHandler();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        try{
            String jsonRequest = jsonBuilder.toString();
            Apartment transientApartment = objectMapper.readValue(jsonRequest, Apartment.class);
            User user = transientApartment.getReservedBy();
            ResultCode resultCode = apartmentService.reserveApartment(transientApartment.getId(), user);
            String jsonResponse = objectMapper.writeValueAsString(resultCode);
            resp.getWriter().write(jsonResponse);
        }catch (ResourceNotFoundException exception){
            exceptionHandler.handleResourceNotFoundException(resp, exception);
        }catch (AlreadyReservedException exception){
            exceptionHandler.handleAlreadyReservedException(resp, exception);
        }catch (Exception e ){
            exceptionHandler.handleGeneralException(resp, e);
        }
    }
}
