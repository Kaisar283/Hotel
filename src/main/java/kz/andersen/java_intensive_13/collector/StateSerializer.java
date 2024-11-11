package kz.andersen.java_intensive_13.collector;

public interface StateSerializer<T> {
    void saveState(T stateCollector, String filePath);

    T loadState(String filePath, Class<T> clazz);
}
