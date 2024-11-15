package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.enums.ResultCode;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.handler.ExceptionHandler;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.models.Apartment;
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
import static org.mockito.Mockito.*;

public class ReleaseApartmentServletTest {

    @Mock
    private ApartmentService apartmentService;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ObjectMapper objectMapper;
    private ReleaseApartmentServlet servlet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        servlet = new ReleaseApartmentServlet(apartmentService, objectMapper, exceptionHandler);
    }

    @Test
    public void doPostSuccess() throws Exception {
        Apartment apartment = new Apartment(1, 5000.0);
        ResultCode resultCode = ResultCode.SUCCESS;

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(apartment))));
        when(apartmentService.releaseApartment(1)).thenReturn(resultCode);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        servlet.doPost(request, response);

        String jsonResponse = stringWriter.toString();
        assertEquals(objectMapper.writeValueAsString(resultCode), jsonResponse);

        verify(apartmentService).releaseApartment(1);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    public void doPostResourceNotFoundException() throws Exception {
        Apartment apartment = new Apartment(1, 500.0);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(apartment))));
        doThrow(new ResourceNotFoundException("Apartment not found"))
                .when(apartmentService).releaseApartment(1);

        servlet.doPost(request, response);

        verify(exceptionHandler).handleResourceNotFoundException(eq(response), any(ResourceNotFoundException.class));
    }

    @Test
    public void doPostAlreadyReservedException() throws Exception {
        Apartment apartment = new Apartment(1, 500.0);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(apartment))));
        doThrow(new AlreadyReservedException("Apartment already reserved"))
                .when(apartmentService).releaseApartment(1);

        servlet.doPost(request, response);

        verify(exceptionHandler).handleAlreadyReservedException(eq(response), any(AlreadyReservedException.class));
    }

    @Test
    public void doPostGeneralException() throws Exception {
        Apartment apartment = new Apartment(1, 500.0);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(apartment))));
        doThrow(new RuntimeException("Unexpected error"))
                .when(apartmentService).releaseApartment(1);

        servlet.doPost(request, response);

        verify(exceptionHandler).handleGeneralException(eq(response), any(Exception.class));
    }
}
