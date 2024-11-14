package kz.andersen.java_intensive_13.exception;

public class ResourceProcessingException extends RuntimeException{
    public ResourceProcessingException(String message) {
        super(message);
    }
}
