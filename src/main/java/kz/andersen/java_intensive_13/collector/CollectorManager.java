package kz.andersen.java_intensive_13.collector;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CollectorManager<T> implements StateSerializer<T>{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void saveState(T stateCollector, String filePath) {
        try(FileWriter fileWriter = new FileWriter(filePath, true);) {
            fileWriter.append(System.lineSeparator());
            objectMapper.findAndRegisterModules();
            objectMapper.writeValue(fileWriter, stateCollector);
        } catch (StreamWriteException | DatabindException e) {
            System.out.println("Error converting stateCollector to JSON");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error writing to file");
            throw new RuntimeException(e);
        }
    }


    @Override
    public T loadState(String filePath, Class<T> clazz) {
        try {
            objectMapper.findAndRegisterModules();
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
