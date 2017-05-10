package de.heidegger.phillip;

import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServer;
import de.heidegger.phillip.events.ClassMapper;
import de.heidegger.phillip.events.EventSocket;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


/**
 * HOW THE BACKEND WORKS
 *
 * The Backend of the Chat Program is based on an EventSourcing concept.
 * The client server communication works as follows:
 * 1. As a communication channel a web socket is used. It is specified in
 *    in this file as a constant (HTTP_LOCALHOST_4200).
 * 2. A WebSocket can be used to push messages to the client and to receive
 *    message. All messages are json objects.
 * 3. The Client send commands to the server.
 * 4. The Sever sends events to its the clients.
 * 5. Persistenz is realised by two mechanisms.
 *    a) The events are stored in a file (TO BE DONE)
 *    b) Views, which are based on the events, can store snapshots in files.
 * 6. The task of the ChatServer class is to receive commands from the
 *    WebSockets currently connected, perform a permission check on them,
 *    and convert the to events. Events are handed over to views. When this
 *    conversion happens, the side effect of the command is performed, e.g.
 *    by sending events to all the clients to let them know about a chat
 *    message, etc.
 *
 */
public class ChatBackend {

    /**
     * Specify here to under what URL the angular-cli server is available.
     */
    public static final String HTTP_LOCALHOST_4200 = "http://localhost:4200/";

    /**
     * Specify und which path the WebSocket taking care of the chat messages
     * should be located. Ensure to pick something not required by the angular-cli
     * projekt, e.g. websocket is a bad idea.
     */
    public static final String CHAT_SOCKET = "/chatSocket";

    public static void main(String[] args) {

        // Create a basic Jetty server object that will listen on port 8080.  Note that if you set this to port 0
        // then a randomly available port will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(8080);

        // If you want to serve from the local folder, uncomment his, and add it
        // to the handler list below.
        // ResourceHandler resourceHandler = createResourceHandler();

        ContextHandler wsContext = createWebSocketContextHandler(CHAT_SOCKET, new ChatServer());
        ServletContextHandler proxyHandler = createProxyHandler();

        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
        // The Handler taking care of the WebSocket is added first.
        // So, all requests to /chatSocket are handled by the socket.
        // All else is delegated to the proxy.
        handlers.setHandlers(new Handler[]{ wsContext, proxyHandler, new DefaultHandler()});
        server.setHandler(handlers);

        try {
            // Start things up! By using the server.join() the server thread will join with the current thread.
            // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sets up a handler that delegates all requests to localhost:4200. This
    // is the WebServer as started by Angular-CLI.
    // To work jetty needs at least two threads for the http client. It is
    // configure to 4 here in this code.
    private static ServletContextHandler createProxyHandler() {
        ServletContextHandler proxyHandler = new ServletContextHandler();
        proxyHandler.setContextPath("/");
        proxyHandler.setInitParameter("maxThreads", "4");

        ServletHolder servletHolder = new ServletHolder(new CBProxyServlet());
        servletHolder.setAsyncSupported(true);
        // this line defines to what address the proxy should delegate to.
        servletHolder.setInitParameter("proxyTo", HTTP_LOCALHOST_4200);

        proxyHandler.addServlet(servletHolder, "/");

        return proxyHandler;
    }

    private static ContextHandler createWebSocketContextHandler(String path, ChatServer server) {
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.setCreator((req, resp) -> {
                    ChatClient chatClient = server.createClientInstance();
                    return new EventSocket(chatClient.getSocketReader(),
                            chatClient.getWebSocketHandler(),
                            new ChatClassMapper());
                });
                //factory.register(EventSocket.class);
            }
        };
        ContextHandler wsContext = new ContextHandler();
        wsContext.setContextPath(path);
        wsContext.setHandler(wsHandler);
        return wsContext;
    }

    private static ResourceHandler createResourceHandler() {
        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler resourceHandler = new ResourceHandler();

        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase("./frontend");
        System.out.println("Files are served from: " +
                System.getProperty("user.dir") + "\\frontend");
        return resourceHandler;
    }

    /**
     * Create a class so that something is printed on the console.
     */
    private static class CBProxyServlet extends ProxyServlet.Transparent {

        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            System.out.println(">> init done !");
        }

        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            System.out.println(">>> got a request that is delegated: "
                    + ((req instanceof Request) ? ((Request) req).getRequestURI() : ""));
            super.service(req, res);
        }

    }

    /**
     * Since we do not want to include in the serialization in the websocket always the
     * fully qualified java class name, we provide here an object that tries first die
     * instantiate the objects under the assumption that the name is the fully qualified
     * name. When this fails, it tries to add the package name in which the commands
     * are stored.
     */
    private static class ChatClassMapper implements ClassMapper {
        private static final String[] packages = {"",
                "de.heidegger.phillip.chat.commands.",
                "de.heidegger.phillip.chat.commands.roomCommands.",
                "de.heidegger.phillip.chat.commands.userCommands."
        };

        @Override
        public boolean exists(String shortName) {
            for (String pkg: packages) {
                try {
                    Class.forName(pkg + shortName, false, ClassLoader.getSystemClassLoader());
                    return true;
                } catch (ClassNotFoundException ignored) {
                }
            }
            return false;
        }

        @Override
        public Class<?> load(String shortName) throws ClassNotFoundException {
            for (String pkg: packages) {
                try {
                    return Class.forName(pkg + shortName, true, ClassLoader.getSystemClassLoader());
                } catch (ClassNotFoundException ignored) {
                }
            }
            throw new ClassNotFoundException(shortName);
        }
    }
}
