package de.heidegger.phillip.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.heidegger.phillip.chat.events.Event;
import de.heidegger.phillip.chat.events.SocketIdEvent;
import de.heidegger.phillip.chat.views.EventView;
import de.heidegger.phillip.events.EventHandler;
import de.heidegger.phillip.events.WebSocketHandler;
import de.heidegger.phillip.utils.EMail;
import org.eclipse.jetty.websocket.api.Session;

import java.util.logging.Level;

public class ChatClient implements EventView {
    private final ChatServer server;
    private final long id;
    private final EventHandler socketReader;
    private final WebSocketHandler webSocketHandler = new WebSocketHandler() {
        @Override
        public void onConnect(Session session) {
            ChatClient.this.session = session;
            ChatClient.this.sendEvent(new SocketIdEvent(id));
        }

        @Override
        public void close() {
            ChatClient.this.session = null;
            server.destroyClientInstance(ChatClient.this);
        }
    };

    private Session session;
    private String userName;
    private EMail mail;
    private boolean authenticated;

    public ChatClient(ChatServer server, long id) {
        this.server = server;
        this.id = id;
        this.socketReader = event -> server.handleClientCommands(id, event);
    }

    public String getUserName() {
        return userName;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public EventHandler getSocketReader() {
        return socketReader;
    }

    public WebSocketHandler getWebSocketHandler() {
        return webSocketHandler;
    }

    void promote(EMail mail, String userName) {
        this.userName = userName;
        this.mail = mail;
        this.authenticated = true;
    }

    public void demote() {
        this.userName = null;
        this.mail = null;
        this.authenticated = false;
    }

    public EMail getEmail() {
        return mail;
    }

    public void sendEvent(Object event) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.sendData(mapper.writeValueAsString(new EventContainer(event.getClass()
                    .getSimpleName(), event)));
        } catch (JsonProcessingException e) {
            server.log(Level.WARNING, "error during serialisation of event.", e);
        }
    }

    public String getName() {
        return userName;
    }

    @Override
    public boolean applied(Event event) {
        return true;
    }

    @Override
    public void consume(Event event) {
        // TODO: filter events send to the client
        this.sendEvent(event);
    }

    public long getId() {
        return id;
    }

    public static class EventContainer {
        private final String type;
        private final Object value;

        public EventContainer(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * Low Level method to send strings over the websocket
     *
     * @param str The string to send
     */
    private void sendData(String str) {
        if (this.session != null) {
            this.session.getRemote().sendStringByFuture(str);
        } else {
            server.log(Level.WARNING, "error during sending event to websocket: " + str);
        }
    }

}
