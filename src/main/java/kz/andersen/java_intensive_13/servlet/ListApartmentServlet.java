package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.services.ApartmentService;

import java.io.IOException;
import java.util.List;

@WebServlet("/apartment")
public class ListApartmentServlet extends HttpServlet {
    private final ApartmentService apartmentService;
    private final ObjectMapper objectMapper;

    public ListApartmentServlet(ApartmentService apartmentService,
                                ObjectMapper objectMapper) {
        this.apartmentService = apartmentService;
        this.objectMapper = objectMapper;
    }

    public ListApartmentServlet(){
        this.apartmentService = new ApartmentService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        List<Apartment> allApartments = apartmentService.getAllApartments();
        String requestList = objectMapper.writeValueAsString(allApartments);
        resp.getWriter().write(requestList);
    }
}
