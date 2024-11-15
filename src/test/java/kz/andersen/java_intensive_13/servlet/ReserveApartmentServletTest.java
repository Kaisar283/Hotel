package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.exception.ExceptionHandler;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.models.Client;
import kz.andersen.java_intensive_13.services.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.mockito.*;
import java.io.*;

class ReserveApartmentServletTest {

    @Mock
    private ApartmentService apartmentService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ExceptionHandler exceptionHandler;

    @InjectMocks
    private ReserveApartmentServlet reserveApartmentServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void reserveApartmentSuccess() throws Exception {
        String jsonRequest = "{\"id\":1,\"price\":1000.0,\"reservedBy\":{\"name\":\"John\"},\"reserved\":true}";
        Apartment apartment = new Apartment(1, 1000.0);
        Client client = new Client();
        client.setName("John");
        apartment.setReservedBy(client);
        apartment.setIsReserved(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(objectMapper.readValue(jsonRequest, Apartment.class)).thenReturn(apartment);
        when(apartmentService.reserveApartment(1, client)).thenReturn(ResultCode.SUCCESS);

        reserveApartmentServlet.doPost(request, response);

        verify(apartmentService).reserveApartment(1, client);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void reserveApartmentResourceNotFound() throws Exception {
        String jsonRequest = "{\"id\":1,\"price\":1000.0,\"reservedBy\":{\"name\":\"John\"},\"reserved\":true}";
        Apartment apartment = new Apartment(1, 1000.0);
        Client client = new Client();
        client.setName("John");
        apartment.setReservedBy(client);
        apartment.setIsReserved(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(objectMapper.readValue(jsonRequest, Apartment.class)).thenReturn(apartment);
        doThrow(new ResourceNotFoundException("Apartment not found"))
                .when(apartmentService).reserveApartment(1, client);

        reserveApartmentServlet.doPost(request, response);

        verify(exceptionHandler).handleResourceNotFoundException(eq(response), any(ResourceNotFoundException.class));
    }

    @Test
    void reserveApartmentAlreadyReserved() throws Exception {
        String jsonRequest = "{\"id\":1,\"price\":1000.0,\"reservedBy\":{\"name\":\"John\"},\"reserved\":true}";
        Apartment apartment = new Apartment(1, 1000.0);
        Client client = new Client();
        client.setName("John");
        apartment.setReservedBy(client);
        apartment.setIsReserved(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));
        when(objectMapper.readValue(jsonRequest, Apartment.class)).thenReturn(apartment);
        doThrow(new AlreadyReservedException("Apartment already reserved"))
                .when(apartmentService).reserveApartment(1, client);

        reserveApartmentServlet.doPost(request, response);

        verify(exceptionHandler).handleAlreadyReservedException(eq(response), any(AlreadyReservedException.class));
    }
}