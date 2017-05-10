package de.heidegger.phillip.events;

public interface ClassMapper {

    boolean exists(String shortName);

    Class<?> load(String shortName) throws ClassNotFoundException;

}
