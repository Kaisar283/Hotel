package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.services.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterApartmentServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ApartmentService apartmentService;

    private ObjectMapper objectMapper;
    private RegisterApartmentServlet servlet;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        servlet = new RegisterApartmentServlet(apartmentService, objectMapper);
    }

    @Test
    public void doPost() throws Exception {
        String inputJson = "{\"price\":3000}";
        Apartment apartment = new Apartment(1, 3000);
        Apartment registeredApartment = new Apartment(1, 3000);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        when(apartmentService.registerApartment(any(Apartment.class))).thenReturn(1);
        when(apartmentService.getApartment(1)).thenReturn(Optional.of(registeredApartment));

        servlet.doPost(request, response);

        String outputJson = stringWriter.toString();
        assertEquals(objectMapper.writeValueAsString(registeredApartment), outputJson);

        ArgumentCaptor<Apartment> apartmentCaptor = ArgumentCaptor.forClass(Apartment.class);
        verify(apartmentService).registerApartment(apartmentCaptor.capture());
        assertEquals(apartment.getPrice(), apartmentCaptor.getValue().getPrice());
    }
}