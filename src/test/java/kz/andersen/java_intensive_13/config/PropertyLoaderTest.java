package kz.andersen.java_intensive_13.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PropertyLoaderTest {

    private PropertyLoader propertyLoader;
    private Map<String, Object> mockProperties;

    @BeforeEach
    void setUp() {
        mockProperties = new HashMap<>();
        mockProperties.put("app", Map.of(
                "State", Map.of("filePath", ".\\app-files\\app-state\\collectedState.json"),
                "models", Map.of("apartment", Map.of("reservation-status", Map.of("changeable", "false")))
        ));
        propertyLoader = new PropertyLoader(mockProperties);
    }

    @Test
    void getStateFilePath() {
        String expectedPath = ".\\app-files\\app-state\\collectedState.json";
        assertEquals(expectedPath, propertyLoader.getStateFilePath());
    }

    @Test
    void getStateFilePath_DefaultValue() {
        assertEquals(".\\app-files\\app-state\\collectedState.json", propertyLoader.getStateFilePath());
    }

    @Test
    void getReservationStatusState() {
        assertEquals("false", propertyLoader.getReservationStatusState());
    }

    @Test
    void getReservationStatusState_DefaultValue() {
        propertyLoader = new PropertyLoader() {
            private HashMap<String, Object> loadProperties() {
                return new HashMap<>(mockProperties);
            }
        };
        assertEquals("true", propertyLoader.getReservationStatusState());
    }

    @Test
    public void findValueByKey_returnNull(){
        Object value = propertyLoader.findValueByKey(mockProperties, "", "");
        assertNull(value);
    }
    @Test
    public void findValueByKey_returnValue() {
        Object value = propertyLoader.findValueByKey(mockProperties,
                "app.models.apartment.reservation-status.changeable", "");
        assertEquals("false", value);
    }
}