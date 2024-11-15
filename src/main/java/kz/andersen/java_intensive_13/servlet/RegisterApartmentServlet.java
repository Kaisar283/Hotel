package kz.andersen.java_intensive_13.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.models.Apartment;
import kz.andersen.java_intensive_13.services.ApartmentService;

import java.io.BufferedReader;
import java.io.IOException;


@WebServlet("/apartment/register")
public class RegisterApartmentServlet extends HttpServlet {
    private final ApartmentService apartmentService;
    private final ObjectMapper objectMapper;

    public RegisterApartmentServlet(ApartmentService apartmentService, ObjectMapper objectMapper) {
        this.apartmentService = apartmentService;
        this.objectMapper = objectMapper;
    }

    public RegisterApartmentServlet(){
        this.apartmentService = new ApartmentService();
        this.objectMapper = new ObjectMapper();
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

        String jsonRequest = jsonBuilder.toString();
        Apartment apartment = objectMapper.readValue(jsonRequest, Apartment.class);
        int apartmentId = apartmentService.registerApartment(apartment);
        Apartment registeredApartment = apartmentService.getApartment(apartmentId).get();
        String jsonResponse = objectMapper.writeValueAsString(registeredApartment);
        resp.getWriter().write(jsonResponse);
    }
}
