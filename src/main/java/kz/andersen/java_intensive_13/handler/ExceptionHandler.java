package kz.andersen.java_intensive_13.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import kz.andersen.java_intensive_13.exception.AlreadyReservedException;
import kz.andersen.java_intensive_13.exception.ApiError;
import kz.andersen.java_intensive_13.exception.ResourceNotFoundException;
import kz.andersen.java_intensive_13.exception.ResourceProcessingException;

import java.io.IOException;

public class ExceptionHandler {

    ObjectMapper objectMapper = new ObjectMapper();

    public void handleResourceNotFoundException(HttpServletResponse resp,
                                                 ResourceNotFoundException ex) throws IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ApiError apiError = new ApiError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        String responseException = objectMapper.writeValueAsString(apiError);
        resp.getWriter().write(responseException);
    }

    public void handleResourceProcessingException(HttpServletResponse resp,
                                                 ResourceProcessingException ex) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ApiError apiError = new ApiError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        String responseException = objectMapper.writeValueAsString(apiError);
        resp.getWriter().write(responseException);
    }

    public void handleAlreadyReservedException(HttpServletResponse response,
                                               AlreadyReservedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiError apiError = new ApiError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        String responseException = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(responseException);
    }

    public void handleGeneralException(HttpServletResponse response,
                                       Exception exception) throws IOException{
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiError apiError = new ApiError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        String responseException = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(responseException);
    }
}
