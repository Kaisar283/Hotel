package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.handler.ExceptionHandler;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.services.ApartmentService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/apartment/sort")
public class SortApartmentServlet extends HttpServlet {

    private final ApartmentService apartmentService;
    private final ObjectMapper objectMapper;
    private final ExceptionHandler exceptionHandler;

    public SortApartmentServlet(ApartmentService apartmentService,
                                ObjectMapper objectMapper,
                                ExceptionHandler exceptionHandler) {
        this.apartmentService = apartmentService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    public SortApartmentServlet() {
        this.apartmentService = new ApartmentService();
        this.objectMapper = new ObjectMapper();
        this.exceptionHandler = new ExceptionHandler();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, String[]> parameterMap = req.getParameterMap();

        try {
            if (!parameterMap.containsKey("page") || !parameterMap.containsKey("page-size") || !parameterMap.containsKey("sorted-by")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Integer page = Integer.parseInt(parameterMap.get("page")[0]);
            Integer pageSize = Integer.parseInt(parameterMap.get("page-size")[0]);
            String sortingParameter = parameterMap.get("sorted-by")[0];

            switch (sortingParameter) {
                case ("price") -> {
                    List<Apartment> apartmentsSortedByPrice = apartmentService.getApartmentsSortedByPrice(page, pageSize);
                    String jsonResponse = objectMapper.writeValueAsString(apartmentsSortedByPrice);
                    resp.getWriter().write(jsonResponse);
                }
                case ("id") -> {
                    List<Apartment> apartmentsSortedById = apartmentService.getApartmentsSortedById(page, pageSize);
                    String jsonResponse = objectMapper.writeValueAsString(apartmentsSortedById);
                    resp.getWriter().write(jsonResponse);
                }
                case ("reservation-status") -> {
                    List<Apartment> apartmentSortedByReservationStatus =
                            apartmentService.getApartmentSortedByReservationStatus(page, pageSize);
                    String jsonResponse = objectMapper.writeValueAsString(apartmentSortedByReservationStatus);
                    resp.getWriter().write(jsonResponse);
                }
                case ("client-name") -> {
                    List<Apartment> apartmentSortedByClientName = apartmentService.getApartmentSortedByClientName(page, pageSize);
                    String jsonResponse = objectMapper.writeValueAsString(apartmentSortedByClientName);
                    resp.getWriter().write(jsonResponse);
                }
                default -> resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
