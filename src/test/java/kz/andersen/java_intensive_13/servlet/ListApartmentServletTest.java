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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListApartmentServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ApartmentService apartmentService;

    private ObjectMapper objectMapper;
    private ListApartmentServlet servlet;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        servlet = new ListApartmentServlet(apartmentService, objectMapper);
    }

    @Test
    void doGet() throws Exception{
        List<Apartment> apartments = new ArrayList<>();
        apartments.add(new Apartment(1, 4000));
        apartments.add(new Apartment(2, 5000));
        apartments.add(new Apartment(3, 3000));

        when(apartmentService.getAllApartments()).thenReturn(apartments);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        servlet.doGet(request, response);

        String jsonResponse = stringWriter.toString();

        String expectedJson = objectMapper.writeValueAsString(apartments);
        assertEquals(expectedJson, jsonResponse);

        verify(apartmentService).getAllApartments();
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }
}