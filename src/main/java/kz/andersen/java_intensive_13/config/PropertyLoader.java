package kz.andersen.java_intensive_13.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PropertyLoader {

    Yaml yaml = new Yaml();
    private final Map<String, Object> properties;
    private final String applicationPropertiesFilePath = "src/main/resources/application.yaml";

    public PropertyLoader(){
        this.properties = loadProperties();
    }

    public PropertyLoader(Map<String, Object> properties){
        this.properties = properties;
    }

    private HashMap<String, Object> loadProperties(){
        try(FileInputStream input = new FileInputStream(applicationPropertiesFilePath)) {
            return yaml.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getProperties(){
        return properties;
    }

    public String getStateFilePath(){
        String targetKey = "app.State.filePath";
        String filePath = (String) findValueByKey(properties, targetKey, "");
        if(filePath == null){
            filePath = ".\\app-files\\app-state\\collectedState.json";
        }
        return filePath;
    }

    public String getReservationStatusState(){
        String targetKey = "app.models.apartment.reservation-status.changeable";
        String status = (String) findValueByKey(properties, targetKey, "");
        if (status == null) {
            status = "true";
        }
        return status;
    }

    public void setReservationStatusState(boolean value){
        String targetKey = "app.models.apartment.reservation-status.changeable";
        String newValue = String.valueOf(value);
        setNestedValue(properties, targetKey, newValue);
        try {
            saveYamlFile(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object findValueByKey(Map<String, Object> map, String targetKey, String parentKey) {
        return map.entrySet().stream()
                .map(entry -> {
                    String currentKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
                    if (currentKey.equals(targetKey)) {
                        return entry.getValue();
                    }
                    if (entry.getValue() instanceof Map) {
                        return findValueByKey((Map<String, Object>) entry.getValue(), targetKey, currentKey);
                    }
                    return null;
                })
                .filter(result -> result != null)
                .findFirst()
                .orElse(null);
    }

    private void setNestedValue(Map<String, Object> map, String targetKey, Object newValue) {
        String[] keys = targetKey.split("\\.");

        Optional<Map<String, Object>> optionalMap = Arrays.stream(keys, 0, keys.length - 1)
                .reduce(Optional.of(map), (optMap, key) -> optMap
                        .filter(m -> m.get(key) instanceof Map)
                        .map(m -> (Map<String, Object>) m.get(key)), (a, b) -> b);

        optionalMap.ifPresent(finalMap -> finalMap.put(keys[keys.length - 1], newValue));
    }

    private void saveYamlFile(Map<String, Object> yamlMap) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        FileWriter writer = new FileWriter(applicationPropertiesFilePath);
        yaml.dump(yamlMap, writer);
        writer.close();
    }
}
