package de.heidegger.phillip.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * WebSocket that takes Json encoded events, parses them
 * and hands the event instance to an EventHandler object
 * that processes the Events further.
 * <p>
 * The EventHandler is final to ensure all communication of the
 * WebSocket is handled correctly. This way, it is not possible to
 * add a handler to late.
 * If you want to delegate events to multiple handlers, you can easily
 * use an event fan.
 * The same holds true for the WebSocketHandler. It is required to
 * collect the WebSocketSession that is handed over to the onWebSocketText
 * method. We need this object to send back data over the WebSocket.
 */
@WebSocket
public class EventSocket implements EventHandler {
	private static final Logger logger = Logger.getLogger(EventSocket.class.getSimpleName());

	static {
		logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
	}

	private final EventHandler eventHandler;
	private final WebSocketHandler webSocketHandler;
    private final ClassMapper classMapper;
    private Session session;

	public EventSocket(EventHandler handler, WebSocketHandler webSocketHandler,
					   ClassMapper classMapper) {
		this.eventHandler = handler;
		this.webSocketHandler = webSocketHandler;
		this.classMapper = classMapper;
	}

    @OnWebSocketConnect
    public void onConnect(Session session) {
	    webSocketHandler.onConnect(session);
    }

    @OnWebSocketClose
	public void onWebSocketClose(Session session, int statusCode, String reason) {
		webSocketHandler.close();
	}

	@OnWebSocketMessage
	public void onWebSocketText(Session session, String message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode jsonNode = mapper.readTree(message);
			JsonNode typeNode = jsonNode.get("type");
			if (typeNode.isTextual()) {
				String type = typeNode.asText();
                instantiateCommandOrEvent(mapper, jsonNode, type);
            } else {
				logger.log(Level.WARNING, "No Json message from client: " + message + ". Message is " +
								"ignored.");
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "No Json message from client: " + message + ". Message is ignored" +
							".", e);
        }
    }

    private void instantiateCommandOrEvent(ObjectMapper mapper, JsonNode jsonNode, String type)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        try {
            if (classMapper.exists(type)) {
                Class<?> eventClass = classMapper.load(type);
                if (eventClass.isAnnotationPresent(EventMarker.class) || eventClass.isAnnotationPresent(CommandMarker.class)) {
                    Object eventInstance = mapper.treeToValue(jsonNode.get("value"), eventClass);
                    eventHandler.handle(eventInstance);
                }
            } else {
                logger.log(Level.WARNING, "Type detected but no class was found for event type: " +
                                type);
            }
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, "Type detected but no class was found for event type: " +
                    type);
        }
    }

    @Override
	public boolean handle(Object event) {
		return true;
	}

}