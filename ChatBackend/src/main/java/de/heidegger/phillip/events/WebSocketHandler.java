package de.heidegger.phillip.events;

import org.eclipse.jetty.websocket.api.Session;

public interface WebSocketHandler {

    void onConnect(Session session);

    void close();

}
