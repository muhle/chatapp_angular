package de.heidegger.phillip.chat.views;

import de.heidegger.phillip.chat.events.Event;

import java.util.Iterator;

public interface EventView {

    boolean applied(Event event);

    void consume(Event event);

    public default void consume(Iterator<Event> events) {
        while (events.hasNext()) {
            consume(events.next());
        }
    }
}
