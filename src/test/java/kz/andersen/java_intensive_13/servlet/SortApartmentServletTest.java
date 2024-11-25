package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.handler.ExceptionHandler;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.services.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SortApartmentServletTest {

    private SortApartmentServlet servlet;
    private ApartmentService apartmentService;
    private ObjectMapper objectMapper;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() {
        apartmentService = mock(ApartmentService.class);
        objectMapper = new ObjectMapper();
        servlet = new SortApartmentServlet(apartmentService, objectMapper, new ExceptionHandler());

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        try {
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void sortByPrice() throws Exception {
        when(request.getParameterMap()).thenReturn(Map.of(
                "page", new String[]{"1"},
                "page-size", new String[]{"5"},
                "sorted-by", new String[]{"price"}
        ));

        List<Apartment> sortedApartments = List.of(
                new Apartment(1000.0),
                new Apartment(2000.0)
        );
        when(apartmentService.getApartmentsSortedByPrice(1, 5)).thenReturn(sortedApartments);

        servlet.doGet(request, response);

        verify(apartmentService).getApartmentsSortedByPrice(1, 5);
        String expectedResponse = objectMapper.writeValueAsString(sortedApartments);
        assertEquals(expectedResponse, responseWriter.toString().trim());
    }

    @Test
    void sortById() throws Exception {
        when(request.getParameterMap()).thenReturn(Map.of(
                "page", new String[]{"1"},
                "page-size", new String[]{"5"},
                "sorted-by", new String[]{"id"}
        ));

        List<Apartment> sortedApartments = List.of(
                new Apartment(1500.0),
                new Apartment(3000.0)
        );
        when(apartmentService.getApartmentsSortedById(1, 5)).thenReturn(sortedApartments);

        servlet.doGet(request, response);

        verify(apartmentService).getApartmentsSortedById(1, 5);
        String expectedResponse = objectMapper.writeValueAsString(sortedApartments);
        assertEquals(expectedResponse, responseWriter.toString().trim());
    }

    @Test
    void sortByReservationStatus() throws Exception {
        when(request.getParameterMap()).thenReturn(Map.of(
                "page", new String[]{"1"},
                "page-size", new String[]{"5"},
                "sorted-by", new String[]{"reservation-status"}
        ));

        List<Apartment> sortedApartments = List.of(
                new Apartment(1000.0),
                new Apartment(2000.0)
        );
        when(apartmentService.getApartmentSortedByReservationStatus(1, 5)).thenReturn(sortedApartments);

        servlet.doGet(request, response);

        verify(apartmentService).getApartmentSortedByReservationStatus(1, 5);
        String expectedResponse = objectMapper.writeValueAsString(sortedApartments);
        assertEquals(expectedResponse, responseWriter.toString().trim());
    }

    @Test
    void invalidSortParameter() throws Exception {
        when(request.getParameterMap()).thenReturn(Map.of(
                "page", new String[]{"1"},
                "page-size", new String[]{"5"},
                "sorted-by", new String[]{"unknown-parameter"}
        ));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("", responseWriter.toString().trim());
    }

    @Test
    void missingParameters() throws Exception {
        when(request.getParameterMap()).thenReturn(Map.of());

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("", responseWriter.toString().trim());
    }
}
